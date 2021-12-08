package edu.utep.pw.ga.util

import edu.utep.pw.ga.*
import java.io.File
import java.util.*

class ParametersFileReader {
    private val parametersFile: File
    val parameters = ArrayList<Parameter>()
    val values = ArrayList<Value>()
    private val letters = charArrayOf(
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z'
    )
    private var first = 0
    private var second = 0
    private var third = 0
    fun readFile() {
        if (Config.isParamsFile) readParamsFile() else readCountsFile()
    }

    private fun readParamsFile() {
        try {
            val fileScanner = Scanner(parametersFile)
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine().trim { it <= ' ' }
                if (line.isEmpty()) continue
                val lineScanner = Scanner(line)
                lineScanner.useDelimiter("\\s*:\\s*")
                val parameter = Parameter(lineScanner.next().trim { it <= ' ' })
                val validValues = lineScanner.next().trim { it <= ' ' }
                val validValuesScanner = Scanner(validValues)
                validValuesScanner.useDelimiter("\\s*,\\s*")
                while (validValuesScanner.hasNext()) {
                    val valueName = validValuesScanner.next().trim { it <= ' ' }
                    if (valueName.isEmpty()) continue
                    val value = Value(valueName, parameter)
                    values.add(value)
                    parameter.validValues += (value)
                }
                parameters.add(parameter)
            }
            fileScanner.close()
        } catch (ex: Exception) {
            throw RuntimeException("An error occurred during input file read, verify the format", ex)
        }
    }

    private fun readCountsFile() {
        try {
            val fileScanner = Scanner(parametersFile)
            while (fileScanner.hasNextLine()) {
                val line = fileScanner.nextLine().trim { it <= ' ' }
                if (line.isEmpty()) continue
                val lineScanner = Scanner(line)
                lineScanner.useDelimiter("\\s*:\\s*")
                val parameterCount = lineScanner.nextInt()
                val valueCount = lineScanner.nextInt()
                for (i in 0 until parameterCount) {
                    val parameter = Parameter(nextParameterName)
                    for (j in 0 until valueCount) {
                        val value = Value(parameter.name + j, parameter)
                        values.add(value)
                        parameter.validValues += value
                    }
                    parameters.add(parameter)
                }
            }
            fileScanner.close()
        } catch (ex: Exception) {
            throw RuntimeException("An error occurred during input file read, verify the format", ex)
        }
    }

    private val nextParameterName: String
        get() {
            val parameterName = "" + letters[first] + "" + letters[second] + "" + letters[third]
            third++
            if (third >= letters.size) {
                third = 0
                second++
                if (second >= letters.size) {
                    second = 0
                    first++
                    if (first >= letters.size) {
                        first = 0
                    }
                }
            }
            return parameterName
        }

    companion object {
        private const val inputFileName = "input.txt"
    }

    init {
        val filePath = System.getProperty("user.dir") + File.separator + inputFileName
        parametersFile = File(filePath)
        if (!parametersFile.exists()) throw RuntimeException(
            "Parameters File ($inputFileName) does not exist, it should be placed at " + System.getProperty(
                "user.dir"
            )
        )
    }
}