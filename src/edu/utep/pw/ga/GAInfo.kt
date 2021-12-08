package edu.utep.pw.ga

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class GAInfo(var population: Array<Individual>, var lastTwoSelectedParentRanks: IntArray) {
    var generation = 0
    var populationFitness = 0
    var solution: Individual? = null
    var lastBestFitness = 0
    var noImprovementCount = 0
}