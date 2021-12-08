package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.util.Config

class MultipleCrossover(private var crossoverPoints: IntArray,
                        gaInfo: GAInfo, domainInfo: DomainInfo) :
    CrossoverStrategy(gaInfo, domainInfo) {
    companion object {
        private var NUM_CROSSOVER_POINTS = 0

        init {
            try {
                NUM_CROSSOVER_POINTS = Config.getUserDefinedValue("NumberCrossoverPoints").toInt()
            } catch (ex: Exception) {
                throw RuntimeException(
                    "NumberCrossoverPoints could not be determined, verify the configuration file",
                    ex
                )
            }
        }
    }

    public override fun getCrossoverPoints(chromosomeSize: Int): IntArray {
        if (crossoverPoints != null) return crossoverPoints
        crossoverPoints = IntArray(NUM_CROSSOVER_POINTS)
        val increments = Math.floor((chromosomeSize.toFloat() / (NUM_CROSSOVER_POINTS + 1).toFloat()).toDouble())
            .toInt()
        for (i in 0 until NUM_CROSSOVER_POINTS) {
            crossoverPoints[i] = (i + 1) * increments
        }
        return crossoverPoints
    }

    override val crossoverFlag: Boolean
        get() = true
}