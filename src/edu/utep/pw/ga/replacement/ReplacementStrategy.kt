package edu.utep.pw.ga.replacement

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.FitnessComparator
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import java.util.*

abstract class ReplacementStrategy(protected var gaInfo: GAInfo, protected var domainInfo: DomainInfo) : Replacement {
    private fun insertIndividual(individual: Individual) {
        var index = Arrays.binarySearch(gaInfo.population, individual, FitnessComparator())
        if (index < 0) {
            var insertIndex = -index - 1
            if (insertIndex == gaInfo.population.size) insertIndex--
            System.arraycopy(
                gaInfo.population,
                insertIndex,
                gaInfo.population,
                insertIndex + 1,
                gaInfo.population.size - 1 - insertIndex
            )
            gaInfo.population[insertIndex] = individual
        } else {
            if (index == gaInfo.population.size) index--
            System.arraycopy(
                gaInfo.population,
                index,
                gaInfo.population,
                index + 1,
                gaInfo.population.size - 1 - index
            )
            gaInfo.population[index] = individual
        }
    }

    private fun removeIndividual(index: Int) {
        System.arraycopy(
            gaInfo.population,
            index + 1,
            gaInfo.population,
            index,
            gaInfo.population.size - 1 - index
        )
    }

    override fun replaceSingleIndividual(removeIndex: Int, newIndividual: Individual) {
        var populationFitness = gaInfo.populationFitness
        populationFitness -= gaInfo.population[removeIndex].fitness
        removeIndividual(removeIndex)
        populationFitness += newIndividual.fitness
        insertIndividual(newIndividual)
        gaInfo.populationFitness = populationFitness
    }

    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.gaInfo = gaInfo
        this.domainInfo = domainInfo
    }
}