package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

interface Crossover {
    fun crossOver(individual1: Individual, individual2: Individual): Array<Individual>
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}