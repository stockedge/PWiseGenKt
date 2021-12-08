package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.util.Config
import edu.utep.pw.ga.util.RandomGenerator
import java.util.*

class MultipleRandomCrossover(gaInfo: GAInfo, domainInfo: DomainInfo) : CrossoverStrategy(gaInfo, domainInfo) {
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
        val crossoverPoints = IntArray(NUM_CROSSOVER_POINTS)
        for (i in 0 until NUM_CROSSOVER_POINTS) {
            crossoverPoints[i] = RandomGenerator.getRandomInt(0, chromosomeSize)
        }
        Arrays.sort(crossoverPoints)
        return crossoverPoints
    }

    override val crossoverFlag: Boolean
        get() = true
}