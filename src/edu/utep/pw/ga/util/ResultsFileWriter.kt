package edu.utep.pw.ga.util

import java.io.File
import java.io.FileWriter

class ResultsFileWriter {
    private var fw: FileWriter? = null
    fun write(line: String) {
        try {
            fw!!.write(
                """
    $line
    
    """.trimIndent()
            )
        } catch (ex: Exception) {
            try {
                fw!!.close()
            } catch (ignored: Exception) {
            }
            throw RuntimeException("Error while trying to write to " + outputFileName, ex)
        }
    }

    fun close() {
        try {
            fw!!.close()
        } catch (ignored: Exception) {
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        close()
    }

    companion object {
        private const val outputFileName = "results.txt"
    }

    init {
        try {
            val filePath = System.getProperty("user.dir") + File.separator + outputFileName
            val resultsFile = File(filePath)
            if (resultsFile.isFile) resultsFile.delete()
            fw = FileWriter(resultsFile, true)
        } catch (ex: Exception) {
            throw RuntimeException("Error while trying to create the ResultsFileWriter", ex)
        }
    }
}