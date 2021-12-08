package edu.utep.pw.ga

import lombok.Getter
import lombok.Setter
import java.util.*

@Getter
@Setter
class Individual(val genes: IntArray) {
    var pairCount = -1
    var fitness = -1
    fun clone(): Individual {
        val genes = Arrays.copyOf(genes, genes.size)
        return Individual(genes)
    }
}