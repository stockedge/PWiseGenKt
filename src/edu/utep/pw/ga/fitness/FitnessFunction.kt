package edu.utep.pw.ga.fitness

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo

abstract class FitnessFunction(protected var gaInfo: GAInfo, protected var domainInfo: DomainInfo) : Fitness {
    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.gaInfo = gaInfo
        this.domainInfo = domainInfo
    }
}