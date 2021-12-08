package edu.utep.pw.ga.initialization

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

interface PopulationInitializer {
    fun createPopulation(): Array<Individual>
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}