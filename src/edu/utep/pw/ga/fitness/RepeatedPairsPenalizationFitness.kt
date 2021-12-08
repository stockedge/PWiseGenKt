package edu.utep.pw.ga.fitness

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.Config

class RepeatedPairsPenalizationFitness(gaInfo: GAInfo, domainInfo: DomainInfo) : FitnessFunction(gaInfo, domainInfo) {
    override fun calculateFitness(individual: Individual): Int {
        var pairs = 0
        var repeated = 0
        val genes = individual.genes
        val pairsCaptured = Array(domainInfo.values.size) { IntArray(domainInfo.values.size) }
        for (testCase in 0 until TEST_SET_SIZE_GOAL) {
            val shift = domainInfo.parameters.size * testCase
            for (param1 in domainInfo.parameters.indices) {
                for (param2 in param1 + 1 until domainInfo.parameters.size) {
                    val allele1 = genes[param1 + shift]
                    val allele2 = genes[param2 + shift]
                    if (pairsCaptured[allele1][allele2] == 0) {
                        pairs++
                    } else if (pairsCaptured[allele1][allele2] > 5) {
                        repeated++
                    }
                    pairsCaptured[allele1][allele2]++
                }
            }
        }
        return pairs - repeated
    }

    companion object {
        private val TEST_SET_SIZE_GOAL = Config.testSetSize
    }
}