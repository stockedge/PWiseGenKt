package edu.utep.pw.ga.replacement

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.Config

class WeakerIndividualsReplacement(gaInfo: GAInfo, domainInfo: DomainInfo) : ReplacementStrategy(gaInfo, domainInfo) {
    override fun replaceIndividuals(offspring: Array<Individual>) {

        //Select two for removal
        val removals = IntArray(2)
        removals[0] = POPULATION_SIZE - 1
        removals[1] = POPULATION_SIZE - 2
        super.replaceSingleIndividual(removals[0], offspring[0])
        super.replaceSingleIndividual(removals[1], offspring[1])
    }

    companion object {
        private val POPULATION_SIZE = Config.populationSize
    }
}