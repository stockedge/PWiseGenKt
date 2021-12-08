package edu.utep.pw.ga

import lombok.Getter
import lombok.Setter

@Getter
@Setter
class DomainInfo(var parameters: List<Parameter>, var values: List<Value>) {
    var totalPairs = 0
}