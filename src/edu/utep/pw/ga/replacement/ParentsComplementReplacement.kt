package edu.utep.pw.ga.replacement

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.Config

class ParentsComplementReplacement(gaInfo: GAInfo, domainInfo: DomainInfo) : ReplacementStrategy(gaInfo, domainInfo) {
    override fun replaceIndividuals(offspring: Array<Individual>) {

        //Select two for removal
        val removals = IntArray(2)
        removals[0] = POPULATION_SIZE - super.gaInfo.lastTwoSelectedParentRanks[0] - 1
        removals[1] = POPULATION_SIZE - super.gaInfo.lastTwoSelectedParentRanks[1] - 1

        //Best individual elitism
        if (removals[0] == 0) removals[0] = if (removals[1] != 1) 1 else 2
        if (removals[1] == 0) removals[1] = if (removals[0] != 1) 1 else 2
        if (removals[1] > removals[0]) {
            val temp = removals[0]
            removals[0] = removals[1]
            removals[1] = temp
        }
        super.replaceSingleIndividual(removals[0], offspring[0])
        super.replaceSingleIndividual(removals[1], offspring[1])
    }

    companion object {
        private val POPULATION_SIZE = Config.populationSize
    }
}