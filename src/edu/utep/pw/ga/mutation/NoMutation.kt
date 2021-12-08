package edu.utep.pw.ga.mutation

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo

class NoMutation(gaInfo: GAInfo, domainInfo: DomainInfo) : MutationStrategy(gaInfo, domainInfo) {
    override val mutationFlag: Boolean
        get() = false
}