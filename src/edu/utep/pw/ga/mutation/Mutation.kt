package edu.utep.pw.ga.mutation

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

interface Mutation {
    fun mutate(individual: Individual)
    fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo)
}