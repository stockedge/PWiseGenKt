package edu.utep.pw.ga

import lombok.Getter

@Getter
class Value(name: String, owner: Parameter) {
    val id //Id of the parameter value
            : Int = nextValueID
    val name //User-defined name
            : String
    val owner //Owner parameter
            : Parameter

    companion object {
        private var valueID = 0 //Used for id-generator "getNextValueID()"

        @get:Synchronized
        private val nextValueID: Int
            get() = valueID++
    }

    init {
        this.name = name
        this.owner = owner
    }
}