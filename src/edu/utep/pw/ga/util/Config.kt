package edu.utep.pw.ga.util

import edu.utep.pw.ga.crossover.Crossover
import edu.utep.pw.ga.fitness.Fitness
import edu.utep.pw.ga.initialization.PopulationInitializer
import edu.utep.pw.ga.mutation.Mutation
import edu.utep.pw.ga.replacement.Replacement
import edu.utep.pw.ga.selection.Selection
import java.io.File
import java.io.FileInputStream
import java.util.*

object Config {
    private val properties = Properties()
    private const val userDirectory = "user.dir"
    private const val defaultPropertyFileName = "ga-config.xml"

    //General GA configurations
    private var testSize = 0
    var populationSize = 0
        get() {
            if (field != 0) return field
            val sizeConfig = properties.getProperty("PopulationSize")
            return try {
                field = sizeConfig.toInt()
                field
            } catch (ex: NumberFormatException) {
                throw RuntimeException("PopulationSize could not be determined, verify the configuration file", ex)
            }
        }
        private set
    var maxGenerations = 0
        get() {
            if (field != 0) return field
            val maxConfig = properties.getProperty("MaxGenerations")
            return try {
                field = maxConfig.toInt()
                field
            } catch (ex: NumberFormatException) {
                throw RuntimeException("MaxGenerations could not be determined, verify the configuration file", ex)
            }
        }
        private set
    var mutationRate = 0.0
        get() = if (field != 0.0) field else try {
            field = properties.getProperty("MutationRate").toDouble()
            field
        } catch (ex: Exception) {
            throw RuntimeException("MutationRate could not be determined, verify the configuration file", ex)
        }
        private set
    var fitnessFunction: Fitness? = null
    var crossoverStrategy: Crossover? = null
    var parentSelector: Selection? = null
    var replacementStrategy: Replacement? = null
    var mutationStrategy: Mutation? = null
    var populationInitializer: PopulationInitializer? = null
    val isParamsFile: Boolean
        get() = try {
            java.lang.Boolean.parseBoolean(properties.getProperty("IsParamsFile"))
        } catch (ex: Exception) {
            throw RuntimeException("IsParamsFile could not be determined, verify the configuration file", ex)
        }
    val printEveryX: Int
        get() = try {
            properties.getProperty("PrintEveryX").toInt()
        } catch (ex: Exception) {
            throw RuntimeException("PrintEveryX could not be determined, verify the configuration file", ex)
        }
    val testSetSize: Int
        get() {
            if (testSize != 0) return testSize
            val sizeConfig = properties.getProperty("TestSetSize")
            return try {
                testSize = sizeConfig.toInt()
                testSize
            } catch (ex: NumberFormatException) {
                throw RuntimeException("TestSetSize could not be determined, verify the configuration file", ex)
            }
        }
    val numberReproductions: Int
        get() = try {
            properties.getProperty("NumberReproductions").toInt()
        } catch (ex: Exception) {
            throw RuntimeException("NumberReproductions could not be determined, verify the configuration file", ex)
        }
    val immigrantEveryX: Int
        get() = try {
            properties.getProperty("ImmigrantEveryX").toInt()
        } catch (ex: Exception) {
            throw RuntimeException("ImmigrantEveryX could not be determined, verify the configuration file", ex)
        }

    fun getUserDefinedValue(property: String): String {
        return properties.getProperty(property)
            ?: throw RuntimeException("Property $property could not be determined, verify the configuration file")
    }

    init {
        try {
            val propertyFileName = System.getProperty(userDirectory) + File.separator + defaultPropertyFileName
            val propFile = FileInputStream(propertyFileName)
            properties.loadFromXML(propFile)
        } catch (e: Exception) {
            throw RuntimeException("Error loading configuration file.", e)
        }
    }
}