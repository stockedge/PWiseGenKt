package edu.utep.pw.ga.fitness

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

interface Fitness {
    fun calculateFitness(individual: Individual): Int
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}