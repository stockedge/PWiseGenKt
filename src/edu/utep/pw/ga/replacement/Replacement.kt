package edu.utep.pw.ga.replacement

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

interface Replacement {
    fun replaceIndividuals(offspring: Array<Individual>)
    fun replaceSingleIndividual(removeIndex: Int, newIndividual: Individual)
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}