package edu.utep.pw.ga

import lombok.Getter

@Getter
class Parameter(name: String) {
    val id: Int
    val name: String
    var validValues: List<Value> = ArrayList()

    companion object {
        private var parameterID = 0 //Used for id-generator "getNextParameterID()"

        @get:Synchronized
        private val nextParameterID: Int
            get() = parameterID++
    }

    init {
        id = nextParameterID
        this.name = name
    }
}