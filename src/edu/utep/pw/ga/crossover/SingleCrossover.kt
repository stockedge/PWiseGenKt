package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import kotlin.math.floor

class SingleCrossover(gaInfo: GAInfo, domainInfo: DomainInfo) : CrossoverStrategy(gaInfo, domainInfo) {
    private var crossoverPoint = 0
    public override fun getCrossoverPoints(chromosomeSize: Int): IntArray {
        if (crossoverPoint != 0) return intArrayOf(crossoverPoint)
        crossoverPoint = floor((chromosomeSize.toFloat() / 2.toFloat()).toDouble()).toInt()
        return intArrayOf(crossoverPoint)
    }

    override val crossoverFlag: Boolean
        get() = true
}