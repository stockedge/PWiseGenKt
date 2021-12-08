package edu.utep.pw.ga

class FitnessComparator : Comparator<Individual> {
    override fun compare(i1: Individual, i2: Individual): Int {
        val fitness1 = i1.fitness
        val fitness2 = i2.fitness
        return fitness2.compareTo(fitness1)
    }
}