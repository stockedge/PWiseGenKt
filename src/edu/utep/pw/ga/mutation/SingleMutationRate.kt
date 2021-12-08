package edu.utep.pw.ga.mutation

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.util.Config
import edu.utep.pw.ga.util.RandomGenerator

class SingleMutationRate(gaInfo: GAInfo, domainInfo: DomainInfo) : MutationStrategy(gaInfo, domainInfo) {
    override val mutationFlag: Boolean
        get() {
            if (Config.mutationRate == 0.0) return false
            var rate = (1.0 / Config.mutationRate).toInt()
            if (rate == 0) rate = 1
            val rand = RandomGenerator.getRandomInt(1, rate)
            return rand == rate
        }
}