package org.example.models

import org.example.ui.common.PrettyPrint

/**
 * Data class representing a mineral with various properties.
 *
 * @property name The name of the mineral.
 * @property luster A list of luster types (e.g., metallic, vitreous).
 * @property color A list of colors the mineral can have.
 * @property hardnessMin The minimum hardness of the mineral on the Mohs scale (1-10).
 * @property hardnessMax The maximum hardness of the mineral on the Mohs scale (1-10).
 * @property fracture The type of fracture the mineral exhibits (e.g., conchoidal, uneven).
 */
class Mineral (
    var name:String? = null,
    var luster:List<String> = emptyList(),
    var color:List<String> = emptyList(),
    var hardnessMin: Double? = null, //measured with Mohs scale. 1 is the softest, 10 is the hardest.
    var hardnessMax: Double? = null,
    var fracture:String? = null
){

    /** Pretty-printed representation of the mineral with labeled fields. */
    override fun toString(): String =
        PrettyPrint.mineralRowLabeled(this)
}