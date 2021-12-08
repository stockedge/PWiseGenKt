package edu.utep.pw.ga.selection

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo

abstract class ParentSelector(protected var gaInfo: GAInfo, protected var domainInfo: DomainInfo) : Selection {
    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.gaInfo = gaInfo
        this.domainInfo = domainInfo
    }
}