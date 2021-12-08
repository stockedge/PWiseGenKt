package edu.utep.pw.ga.mutation

import edu.utep.pw.ga.DomainInfo
import edu.utep.pw.ga.GAInfo
import edu.utep.pw.ga.Individual
import edu.utep.pw.ga.util.Config
import edu.utep.pw.ga.util.RandomGenerator

class SmartMutation(gaInfo: GAInfo, domainInfo: DomainInfo) : MutationStrategy(gaInfo, domainInfo, ) {
    private enum class SmartMutationType {
        PROBABILITY, AFTER_X, BEFORE_X, EVERY_X, NO_IMPROVEMENT_X
    }

    companion object {
        private val TEST_SET_SIZE_GOAL = Config.testSetSize
        private var USE_SIMILARITY_MUTATION = false
        private var SIMILARITY_MUTATION_TYPE: SmartMutationType? = null
        private var SIMILARITY_MUTATION_VALUE = 0.0
        private var THRESHOLD_SIMILARITY = 0
        private var REPLACE_WITH_SMART_TEST_CASE = false
        private var USE_VALUE_OCCURRENCE_MUTATION = false
        private var VALUE_OCCURRENCE_MUTATION_TYPE: SmartMutationType? = null
        private var VALUE_OCCURRENCE_MUTATION_VALUE = 0.0
        private var BALANCE_VALUE_OCCURRENCES = false
        private var USE_PAIR_OCCURRENCE_MUTATION = false
        private var PAIR_OCCURRENCE_MUTATION_TYPE: SmartMutationType? = null
        private var PAIR_OCCURRENCE_MUTATION_VALUE = 0.0
        private var THRESHOLD_PAIR_OCCURRENCES = 0

        init {
            try {
                USE_SIMILARITY_MUTATION =
                    java.lang.Boolean.parseBoolean(Config.getUserDefinedValue("UseSimilarityMutation"))
                SIMILARITY_MUTATION_TYPE =
                    SmartMutationType.valueOf(Config.getUserDefinedValue("SimilarityMutationType"))
                SIMILARITY_MUTATION_VALUE = Config.getUserDefinedValue("SimilarityMutationValue").toDouble()
                THRESHOLD_SIMILARITY = Config.getUserDefinedValue("ThresholdSimilarity").toInt()
                REPLACE_WITH_SMART_TEST_CASE =
                    java.lang.Boolean.parseBoolean(Config.getUserDefinedValue("ReplaceWithSmartTestCase"))
                USE_VALUE_OCCURRENCE_MUTATION =
                    java.lang.Boolean.parseBoolean(Config.getUserDefinedValue("UseValueOccurrenceMutation"))
                VALUE_OCCURRENCE_MUTATION_TYPE =
                    SmartMutationType.valueOf(Config.getUserDefinedValue("ValueOccurrenceMutationType"))
                VALUE_OCCURRENCE_MUTATION_VALUE = Config.getUserDefinedValue("ValueOccurrenceMutationValue").toDouble()
                BALANCE_VALUE_OCCURRENCES =
                    java.lang.Boolean.parseBoolean(Config.getUserDefinedValue("BalanceValueOccurences"))
                USE_PAIR_OCCURRENCE_MUTATION =
                    java.lang.Boolean.parseBoolean(Config.getUserDefinedValue("UsePairOccurrenceMutation"))
                PAIR_OCCURRENCE_MUTATION_TYPE =
                    SmartMutationType.valueOf(Config.getUserDefinedValue("PairOccurrenceMutationType"))
                PAIR_OCCURRENCE_MUTATION_VALUE = Config.getUserDefinedValue("PairOccurrenceMutationValue").toDouble()
                THRESHOLD_PAIR_OCCURRENCES = Config.getUserDefinedValue("ThresholdPairOcurrences").toInt()
            } catch (ex: Exception) {
                throw RuntimeException(
                    "Smart Mutation failed when trying to initialize its values, verify the configuration file",
                    ex
                )
            }
        }
    }

    override val mutationFlag: Boolean
        get() {
            if (Config.mutationRate == 0.0) return false
            var rate = (1.0 / Config.mutationRate).toInt()
            if (rate == 0) rate = 1
            val rand = RandomGenerator.getRandomInt(1, rate)
            return rand == rate
        }

    override fun mutate(individual: Individual) {
        super.mutate(individual)
        startSmartMutations(individual)
    }

    private fun checkValueOccurrences(individual: Individual) {
        val occurrences = IntArray(super.domainInfo.values.size)
        for (i in individual.genes.indices) {
            occurrences[individual.genes[i]]++
        }
        for (iValue in occurrences.indices) {
            if (occurrences[iValue] == 0) {
                val parameter = super.domainInfo.values[iValue].owner
                var valueWithMaxOcurrences = iValue
                for (value in parameter.validValues) {
                    if (occurrences[value.id] > occurrences[valueWithMaxOcurrences]) valueWithMaxOcurrences = value.id
                }
                for (j in individual.genes.indices) {
                    if (individual.genes[j] == valueWithMaxOcurrences) {
                        individual.genes[j] = iValue
                        occurrences[valueWithMaxOcurrences]--
                        occurrences[iValue]++
                        if (!BALANCE_VALUE_OCCURRENCES || occurrences[iValue] >= occurrences[valueWithMaxOcurrences]) break
                    }
                }
            }
        }
    }

    private fun getSmartTestCase(individual: Individual): IntArray {
        val testCase = IntArray(super.domainInfo.parameters.size)
        val occurrences = IntArray(super.domainInfo.values.size)
        for (i in individual.genes.indices) {
            occurrences[individual.genes[i]]++
        }
        for (parameter in super.domainInfo.parameters) {
            var valueWithMinOccurrences = parameter.validValues[0].id
            for (value in parameter.validValues) {
                if (occurrences[value.id] < occurrences[valueWithMinOccurrences]) valueWithMinOccurrences = value.id
            }
            testCase[parameter.id] = valueWithMinOccurrences
        }
        return testCase
    }

    private val randomTestCase: IntArray
        get() {
            val testCase = IntArray(super.domainInfo.parameters.size)
            for (param in super.domainInfo.parameters.indices) {
                val parameter = super.domainInfo.parameters[param]
                val randomIndex = RandomGenerator.getRandomInt(0, parameter.validValues.size - 1)
                val allele = parameter.validValues[randomIndex]
                testCase[param] = allele.id
            }
            return testCase
        }

    private fun replaceSimilarTestCases(individual: Individual) {
        val threshold =
            Math.floor((super.domainInfo.parameters.size.toFloat() * THRESHOLD_SIMILARITY.toFloat() / 100.toFloat()).toDouble())
                .toInt()
        for (tcMain in 0 until TEST_SET_SIZE_GOAL) {
            val shiftMain = super.domainInfo.parameters.size * tcMain
            for (tcCompare in tcMain + 1 until TEST_SET_SIZE_GOAL) {
                val shiftCompare = super.domainInfo.parameters.size * tcCompare
                var similarities = 0
                for (i in super.domainInfo.parameters.indices) {
                    if (individual.genes[i + shiftMain] == individual.genes[i + shiftCompare]) similarities++
                }
                if (similarities > threshold) {
                    if (REPLACE_WITH_SMART_TEST_CASE) System.arraycopy(
                        getSmartTestCase(individual),
                        0,
                        individual.genes,
                        shiftCompare,
                        super.domainInfo.parameters.size
                    ) else System.arraycopy(
                        randomTestCase, 0, individual.genes, shiftCompare, super.domainInfo.parameters.size
                    )
                }
            }
        }
    }

    private fun checkPairOcurrences(individual: Individual) {
        val genes = individual.genes
        val occurrences = Array(super.domainInfo.values.size) { IntArray(super.domainInfo.values.size) }
        for (testCase in 0 until TEST_SET_SIZE_GOAL) {
            val shift = super.domainInfo.parameters.size * testCase
            for (param1 in super.domainInfo.parameters.indices) {
                for (param2 in param1 + 1 until super.domainInfo.parameters.size) {
                    val value1 = genes[param1 + shift]
                    val value2 = genes[param2 + shift]
                    occurrences[value1][value2]++
                }
            }
        }
        for (i in super.domainInfo.values.indices) {
            for (j in i + 1 until super.domainInfo.values.size) {
                val value1 = super.domainInfo.values[i]
                val value2 = super.domainInfo.values[j]
                if (value1.owner == value2.owner) continue
                if (occurrences[i][j] == 0) {
                    for (testCase in 0 until TEST_SET_SIZE_GOAL) {
                        val shift = super.domainInfo.parameters.size * testCase
                        val value1Temp = genes[value1.owner.id + shift]
                        val value2Temp = genes[value2.owner.id + shift]
                        if (occurrences[value1Temp][value2Temp] > THRESHOLD_PAIR_OCCURRENCES) {
                            genes[value1.owner.id + shift] = i
                            genes[value2.owner.id + shift] = j
                            break
                        }
                    }
                }
            }
        }
    }

    private fun startSmartMutations(individual: Individual) {
        if (USE_SIMILARITY_MUTATION) {
            var flag = false
            if (SIMILARITY_MUTATION_TYPE == SmartMutationType.PROBABILITY && SIMILARITY_MUTATION_VALUE != 0.0) {
                val rate = (1.0 / SIMILARITY_MUTATION_VALUE).toInt()
                val rand = RandomGenerator.getRandomInt(0, rate)
                flag = rand == rate
            } else if (SIMILARITY_MUTATION_TYPE == SmartMutationType.AFTER_X) {
                flag = super.gaInfo.generation > SIMILARITY_MUTATION_VALUE
            } else if (SIMILARITY_MUTATION_TYPE == SmartMutationType.BEFORE_X) {
                flag = super.gaInfo.generation < SIMILARITY_MUTATION_VALUE
            } else if (SIMILARITY_MUTATION_TYPE == SmartMutationType.EVERY_X) {
                flag = super.gaInfo.generation % SIMILARITY_MUTATION_VALUE == 0.0
            } else if (SIMILARITY_MUTATION_TYPE == SmartMutationType.NO_IMPROVEMENT_X) {
                flag = super.gaInfo.noImprovementCount >= SIMILARITY_MUTATION_VALUE
            }
            if (flag) replaceSimilarTestCases(individual)
        }
        if (USE_VALUE_OCCURRENCE_MUTATION) {
            var flag = false
            if (VALUE_OCCURRENCE_MUTATION_TYPE == SmartMutationType.PROBABILITY && VALUE_OCCURRENCE_MUTATION_VALUE != 0.0) {
                val rate = (1.0 / VALUE_OCCURRENCE_MUTATION_VALUE).toInt()
                val rand = RandomGenerator.getRandomInt(0, rate)
                flag = rand == rate
            } else if (VALUE_OCCURRENCE_MUTATION_TYPE == SmartMutationType.AFTER_X) {
                flag = super.gaInfo.generation > VALUE_OCCURRENCE_MUTATION_VALUE
            } else if (VALUE_OCCURRENCE_MUTATION_TYPE == SmartMutationType.BEFORE_X) {
                flag = super.gaInfo.generation < VALUE_OCCURRENCE_MUTATION_VALUE
            } else if (VALUE_OCCURRENCE_MUTATION_TYPE == SmartMutationType.EVERY_X) {
                flag = super.gaInfo.generation % VALUE_OCCURRENCE_MUTATION_VALUE == 0.0
            } else if (VALUE_OCCURRENCE_MUTATION_TYPE == SmartMutationType.NO_IMPROVEMENT_X) {
                flag = super.gaInfo.noImprovementCount >= VALUE_OCCURRENCE_MUTATION_VALUE
            }
            if (flag) checkValueOccurrences(individual)
        }
        if (USE_PAIR_OCCURRENCE_MUTATION) {
            var flag = false
            if (PAIR_OCCURRENCE_MUTATION_TYPE == SmartMutationType.PROBABILITY && PAIR_OCCURRENCE_MUTATION_VALUE != 0.0) {
                val rate = (1.0 / PAIR_OCCURRENCE_MUTATION_VALUE).toInt()
                val rand = RandomGenerator.getRandomInt(0, rate)
                flag = rand == rate
            } else if (PAIR_OCCURRENCE_MUTATION_TYPE == SmartMutationType.AFTER_X) {
                flag = super.gaInfo.generation > PAIR_OCCURRENCE_MUTATION_VALUE
            } else if (PAIR_OCCURRENCE_MUTATION_TYPE == SmartMutationType.BEFORE_X) {
                flag = super.gaInfo.generation < PAIR_OCCURRENCE_MUTATION_VALUE
            } else if (PAIR_OCCURRENCE_MUTATION_TYPE == SmartMutationType.EVERY_X) {
                flag = super.gaInfo.generation % PAIR_OCCURRENCE_MUTATION_VALUE == 0.0
            } else if (PAIR_OCCURRENCE_MUTATION_TYPE == SmartMutationType.NO_IMPROVEMENT_X) {
                flag = super.gaInfo.noImprovementCount >= PAIR_OCCURRENCE_MUTATION_VALUE
            }
            if (flag) checkPairOcurrences(individual)
        }
    }
}