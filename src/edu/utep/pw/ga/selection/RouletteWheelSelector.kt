package edu.utep.pw.ga.selection

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.util.Config
import edu.utep.pw.ga.util.RandomGenerator
import java.util.*

class RouletteWheelSelector(gaInfo: GAInfo, domainInfo: DomainInfo) : ParentSelector(gaInfo, domainInfo) {
    override fun selectTwoParents(): IntArray {
        val selected = IntArray(2)
        val cumulativeFitness = IntArray(POPULATION_SIZE)
        cumulativeFitness[0] = super.gaInfo.population[0].fitness
        for (i in 1 until POPULATION_SIZE) {
            cumulativeFitness[i] = cumulativeFitness[i - 1] + super.gaInfo.population[i].fitness
        }

        //Spin the wheel - Parent 1
        var rand = RandomGenerator.getRandomInt(0, super.gaInfo.populationFitness)
        var index = Arrays.binarySearch(cumulativeFitness, rand)
        if (index < 0) index = -index - 1
        selected[0] = index
        do {

            //Spin the wheel - Parent 2
            rand = RandomGenerator.getRandomInt(0, super.gaInfo.populationFitness)
            index = Arrays.binarySearch(cumulativeFitness, rand)
            if (index < 0) index = -index - 1
            selected[1] = index
        } while (selected[0] == selected[1])
        return selected
    }

    companion object {
        private val POPULATION_SIZE = Config.populationSize
    }
}