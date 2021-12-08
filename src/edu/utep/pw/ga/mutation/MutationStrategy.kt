package edu.utep.pw.ga.mutation

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.RandomGenerator

abstract class MutationStrategy(protected var gaInfo: GAInfo, protected var domainInfo: DomainInfo) : Mutation {
    protected abstract val mutationFlag: Boolean
    override fun mutate(individual: Individual) {
        if (!mutationFlag) return
        val geneIndex = RandomGenerator.getRandomInt(
            0,
            individual.genes.size - 1
        ) //Get the position of the gene that will be mutated
        val parameter = domainInfo.parameters[geneIndex % domainInfo.parameters.size]
        val alleleIndex = RandomGenerator.getRandomInt(
            0,
            parameter.validValues.size - 1
        ) //Get the position of the allele/value that will be chosen
        val allele = parameter.validValues[alleleIndex].id
        individual.genes[geneIndex] = allele
    }

    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.gaInfo = gaInfo
        this.domainInfo = domainInfo
    }
}