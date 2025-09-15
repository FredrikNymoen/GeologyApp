package org.example.models

import org.example.ui.common.PrettyPrint

class Mineral (
    var name:String? = null,
    var luster:List<String> = emptyList(),
    var color:List<String> = emptyList(),
    var hardnessMin: Double? = null, //measured with Mohs scale. 1 is the softest, 10 is the hardest.
    var hardnessMax: Double? = null,
    var fracture:String? = null
){
    override fun toString(): String =
        PrettyPrint.mineralRowLabeled(this)
}