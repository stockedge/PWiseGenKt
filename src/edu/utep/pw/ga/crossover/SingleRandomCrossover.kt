package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.util.RandomGenerator

class SingleRandomCrossover(gaInfo: GAInfo, domainInfo: DomainInfo) : CrossoverStrategy(gaInfo, domainInfo) {
    public override fun getCrossoverPoints(chromosomeSize: Int): IntArray {
        return intArrayOf(RandomGenerator.getRandomInt(0, chromosomeSize))
    }

    override val crossoverFlag: Boolean
        get() = true
}