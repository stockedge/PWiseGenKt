package edu.utep.pw.ga

import edu.utep.pw.ga.crossover.SingleCrossover
import edu.utep.pw.ga.fitness.DifferentPairsFitness
import edu.utep.pw.ga.initialization.RandomPopulationInitializer
import edu.utep.pw.ga.mutation.SingleMutationRate
import edu.utep.pw.ga.replacement.WeakerIndividualsReplacement
import edu.utep.pw.ga.selection.RouletteWheelSelector
import edu.utep.pw.ga.util.RandomGenerator
import edu.utep.pw.ga.util.ResultsFileWriter
import edu.utep.pw.ga.util.ParametersFileReader
import java.util.Arrays
import edu.utep.pw.ga.util.Config
import java.awt.Toolkit
import java.lang.StringBuilder
import java.lang.management.ManagementFactory
import kotlin.jvm.JvmStatic
import kotlin.math.ceil

class GeneticAlgorithm(val gaInfo: GAInfo, val domainInfo: DomainInfo) {
    private val fitnessFunction = Config.fitnessFunction
    private val crossoverStrategy = Config.crossoverStrategy
    private val parentSelector = Config.parentSelector
    private val mutationStrategy = Config.mutationStrategy
    private val replacementStrategy = Config.replacementStrategy
    private val fitnessComparator = FitnessComparator()
    private val resultsWriter = ResultsFileWriter()
    private val randomGenes: IntArray
        get() {
            val genes = IntArray(domainInfo.parameters.size * TEST_SET_SIZE_GOAL)
            for (testCase in 0 until TEST_SET_SIZE_GOAL) {
                val shift = domainInfo.parameters.size * testCase
                for (param in domainInfo.parameters.indices) {
                    val parameter = domainInfo.parameters[param]
                    val randomIndex = RandomGenerator.getRandomInt(0, parameter.validValues.size - 1)
                    val allele = parameter.validValues[randomIndex]
                    genes[param + shift] = allele.id
                }
            }
            return genes
        }

    private fun initialize() {

        //Read the input file
        val reader = ParametersFileReader()
        reader.readFile()
        domainInfo.parameters = reader.parameters
        domainInfo.values = reader.values

        //Calculate total pairs
        var totalPairs = 0
        for (i in domainInfo.values.indices) {
            for (j in i + 1 until domainInfo.values.size) {
                val value1 = domainInfo.values[i]
                val value2 = domainInfo.values[j]
                if (value1.owner == value2.owner) //Avoid pair values of the same parameter owner
                    continue
                totalPairs++
            }
        }
        domainInfo.totalPairs = totalPairs
        val populationInitializer = Config.populationInitializer
        populationInitializer!!.setExtraInfo(gaInfo, domainInfo)
        val population = populationInitializer.createPopulation()
        gaInfo.population = population
        gaInfo.generation = 0
        gaInfo.populationFitness = 0
    }

    private fun execute() {

        //Calculate fitness of initial population
        for (individual in gaInfo.population) {
            val individualFitness = fitnessFunction!!.calculateFitness(individual)
            individual.fitness = individualFitness
            val individualPairs = calculatePairCount(individual)
            individual.pairCount = individualPairs
            val populationFitness = gaInfo.populationFitness + individualFitness
            gaInfo.populationFitness = populationFitness
        }

        //Order initial population based on fitness
        Arrays.sort(gaInfo.population, fitnessComparator)
        while (gaInfo.generation <= MAX_GENERATIONS && !solutionFound()) {
            if (gaInfo.generation % PRINT_EVERY_X_GEN == 0) {
                println("Generation: " + gaInfo.generation + " Best Fitness: " + gaInfo.population[0].fitness + ", Pairs: " + gaInfo.population[0].pairCount)
                println("------------------")

                resultsWriter.write(gaInfo.generation.toString() + "," + gaInfo.population[0].fitness)
            }

            //Iterate depending on the number of reproductions in each generation
            for (reproduction in 0 until NUM_REPRODUCTIONS) {

                //Select two parents for reproduction
                val parents = parentSelector!!.selectTwoParents()
                val parent1 = gaInfo.population[parents[0]]
                val parent2 = gaInfo.population[parents[1]]
                gaInfo.lastTwoSelectedParentRanks = parents

                //Get the two children
                val offspring = crossoverStrategy!!.crossOver(parent1, parent2)

                //Mutate
                mutationStrategy!!.mutate(offspring[0])
                mutationStrategy.mutate(offspring[1])

                //Calculate fitness
                offspring[0].fitness = fitnessFunction!!.calculateFitness(offspring[0])
                offspring[1].fitness = fitnessFunction.calculateFitness(offspring[1])

                //Calculate pairs
                offspring[0].pairCount = calculatePairCount(offspring[0])
                offspring[1].pairCount = calculatePairCount(offspring[1])

                //Replacement
                replacementStrategy!!.replaceIndividuals(offspring)
            }

            //Immigrant ?
            if (gaInfo.generation % IMMIGRANT_EVERY_X_GEN == 0) {
                val index = RandomGenerator.getRandomInt(
                    ceil((POPULATION_SIZE / 2).toDouble()).toInt(), POPULATION_SIZE - 1
                )
                val immigrant = Individual(randomGenes)
                replacementStrategy!!.replaceSingleIndividual(index, immigrant)
            }
            if (gaInfo.lastBestFitness >= gaInfo.population[0].fitness) gaInfo.noImprovementCount =
                gaInfo.noImprovementCount + 1 else gaInfo.noImprovementCount = 0
            gaInfo.lastBestFitness = gaInfo.population[0].fitness
            gaInfo.generation = gaInfo.generation + 1
        }
        if (gaInfo.solution != null) {
            println("SOLUTION FOUND!!! in generation " + gaInfo.generation)
            resultsWriter.write("SOLUTION FOUND!!! in generation " + gaInfo.generation)
            printIndividual(gaInfo.solution!!)
        } else {
            println("Max number of generations was reached, Individual with best fitness:")
            resultsWriter.write("Max number of generations was reached, Individual with best fitness:")
            printIndividual(gaInfo.population[0])
        }
    }

    private fun calculatePairCount(individual: Individual): Int {
        var pairs = 0
        val genes = individual.genes
        val pairsCaptured = Array(domainInfo.values.size) { BooleanArray(domainInfo.values.size) }
        for (testCase in 0 until TEST_SET_SIZE_GOAL) {
            val shift = domainInfo.parameters.size * testCase
            for (param1 in domainInfo.parameters.indices) {
                for (param2 in param1 + 1 until domainInfo.parameters.size) {
                    val allele1 = genes[param1 + shift]
                    val allele2 = genes[param2 + shift]
                    if (!pairsCaptured[allele1][allele2]) {
                        pairsCaptured[allele1][allele2] = true
                        pairs++
                    }
                }
            }
        }
        return pairs
    }

    private fun solutionFound(): Boolean {
        for (individual in gaInfo.population) {
            if (domainInfo.totalPairs == individual.pairCount) {
                gaInfo.solution = individual
                return true
            }
        }
        return false
    }

    private fun printIndividual(individual: Individual) {
        val strIndividual = StringBuilder()
        println()
        resultsWriter.write("")
        strIndividual.append("[ (")
        for (i in individual.genes.indices) {
            if (i != 0 && i != individual.genes.size - 1 && i % domainInfo.parameters.size == 0) strIndividual.append("),(") //System.out.print("),(");
            strIndividual.append(domainInfo.values[individual.genes[i]].name)
            if ((i + 1) % domainInfo.parameters.size != 0) strIndividual.append(",")
        }
        strIndividual.append(") ] Fitness: ").append(individual.fitness).append(", Pairs: ")
            .append(individual.pairCount)
        println(strIndividual)
        resultsWriter.write(strIndividual.toString())
        println()
        resultsWriter.write("")
    }

    fun start() {
        val mx = ManagementFactory.getThreadMXBean()
        val start = mx.currentThreadCpuTime
        initialize()
        println("Total pairs: " + domainInfo.totalPairs)
        println("------------------")
        resultsWriter.write("Total pairs: " + domainInfo.totalPairs)
        resultsWriter.write("------------------")
        execute()
        val end = mx.currentThreadCpuTime
        println("Time -> " + (end - start).toDouble() / 1000000000.toDouble() / 60.toDouble() + " min")
        println("Time -> " + (end - start).toDouble() / 1000000000.toDouble() + " seg")
        resultsWriter.write("Time -> " + (end - start).toDouble() / 1000000000.toDouble() / 60.toDouble() + " min")
        resultsWriter.write("Time -> " + (end - start).toDouble() / 1000000000.toDouble() + " seg")
        resultsWriter.close()
    }

    companion object {
        private val PRINT_EVERY_X_GEN = Config.printEveryX
        private val TEST_SET_SIZE_GOAL = Config.testSetSize
        private val POPULATION_SIZE = Config.populationSize
        private val MAX_GENERATIONS = Config.maxGenerations
        private val IMMIGRANT_EVERY_X_GEN = Config.immigrantEveryX
        private val NUM_REPRODUCTIONS = Config.numberReproductions
        @JvmStatic
        fun main(args: Array<String>) {
            val population = arrayOf<Individual>()
            val values = listOf<Value>()
            val parameters = listOf<Parameter>()
            val lastTwoSelectedParentRanks = intArrayOf()
            val gaInfo = GAInfo(population, lastTwoSelectedParentRanks)
            val domainInfo = DomainInfo(parameters, values)
            Config.crossoverStrategy = SingleCrossover(gaInfo, domainInfo)
            Config.fitnessFunction = DifferentPairsFitness(gaInfo, domainInfo)
            Config.mutationStrategy = SingleMutationRate(gaInfo, domainInfo)
            Config.replacementStrategy = WeakerIndividualsReplacement(gaInfo, domainInfo)
            Config.parentSelector = RouletteWheelSelector(gaInfo, domainInfo)
            Config.populationInitializer = RandomPopulationInitializer(domainInfo)
            val algorithm = GeneticAlgorithm(gaInfo, domainInfo)
            algorithm.start()
            Toolkit.getDefaultToolkit().beep()
        }
    }

    init {
        Config.crossoverStrategy?.setExtraInfo(gaInfo, domainInfo)
        Config.fitnessFunction?.setExtraInfo(gaInfo, domainInfo)
        Config.mutationStrategy?.setExtraInfo(gaInfo, domainInfo)
        Config.replacementStrategy?.setExtraInfo(gaInfo, domainInfo)
        Config.parentSelector?.setExtraInfo(gaInfo, domainInfo)
    }
}