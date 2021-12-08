package edu.utep.pw.ga.crossover

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual

abstract class CrossoverStrategy(
    protected var gaInfo: GAInfo,
    protected var domainInfo: DomainInfo
    ) : Crossover {
    protected abstract val crossoverFlag: Boolean
    protected abstract fun getCrossoverPoints(chromosomeSize: Int): IntArray
    override fun crossOver(individual1: Individual, individual2: Individual): Array<Individual> {
        val offspring1 = individual1.clone()
        val offspring2 = individual2.clone()
        if (!crossoverFlag) return arrayOf(offspring1, offspring2)
        val crossoverPoints = getCrossoverPoints(individual1.genes.size)

        //Perform the cross over
        for (i in crossoverPoints.indices) {
            if (i % 2 == 1) continue
            if (i + 1 != crossoverPoints.size) {
                System.arraycopy(
                    individual1.genes,
                    crossoverPoints[i],
                    offspring2.genes,
                    crossoverPoints[i],
                    crossoverPoints[i + 1] - crossoverPoints[i]
                )
                System.arraycopy(
                    individual2.genes,
                    crossoverPoints[i],
                    offspring1.genes,
                    crossoverPoints[i],
                    crossoverPoints[i + 1] - crossoverPoints[i]
                )
            } else {
                System.arraycopy(
                    individual1.genes,
                    crossoverPoints[i],
                    offspring2.genes,
                    crossoverPoints[i],
                    individual1.genes.size - crossoverPoints[i]
                )
                System.arraycopy(
                    individual2.genes,
                    crossoverPoints[i],
                    offspring1.genes,
                    crossoverPoints[i],
                    individual2.genes.size - crossoverPoints[i]
                )
            }
        }
        return arrayOf(offspring1, offspring2)
    }

    override fun setExtraInfo(gaInfo: GAInfo, domainInfo: DomainInfo) {
        this.gaInfo = gaInfo
        this.domainInfo = domainInfo
    }
}