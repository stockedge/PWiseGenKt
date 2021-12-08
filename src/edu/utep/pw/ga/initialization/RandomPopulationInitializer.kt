package edu.utep.pw.ga.initialization

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.Config
import edu.utep.pw.ga.util.RandomGenerator
import java.util.stream.Collectors
import java.util.stream.IntStream

class RandomPopulationInitializer(private var domainInfo: DomainInfo) : PopulationInitializer {
    override fun createPopulation(): Array<Individual> {

        //Generate random population
        return IntStream.range(0, POPULATION_SIZE)
            .mapToObj { Individual(randomGenes) }
            .collect(Collectors.toList())
            .toTypedArray()
    }

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

    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.domainInfo = domainInfo
    }

    companion object {
        private val POPULATION_SIZE = Config.populationSize
        private val TEST_SET_SIZE_GOAL = Config.testSetSize
    }
}