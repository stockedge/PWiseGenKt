package edu.utep.pw.ga.selection

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo

interface Selection {
    fun selectTwoParents(): IntArray
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}