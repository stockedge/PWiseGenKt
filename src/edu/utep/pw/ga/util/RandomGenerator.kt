package edu.utep.pw.ga.util

import java.util.*

object RandomGenerator {
    private val rand = Random()
    fun getRandomInt(min: Int, max: Int): Int {
        val range = max.toLong() - min.toLong() + 1
        val fraction = (range * rand.nextDouble()).toLong()
        return (fraction + min).toInt()
    }

    val randomDouble: Double
        get() = rand.nextDouble()
}