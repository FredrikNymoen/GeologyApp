package org.example.services

import org.example.models.Mineral
import org.example.utils.MineralLoader

class MineralService (
    val source : () -> List<Mineral> = {MineralLoader.loadFromFile()}
){
    private val minerals = mutableListOf<Mineral>() //The list with all the minerals

    init {
        minerals += source()           // update minerals when creating the object
    }

    // --- CRUD on mineral list ---
    fun listAll() : List<Mineral> {
        return minerals.toList() // Returns the minerals as a list
    }

    fun add(mineral: Mineral) {
        minerals.add(mineral)
    }

    fun update(index: Int, mutate: (Mineral) -> Unit): Boolean =
        minerals.getOrNull(index)?.let { mutate(it); true } ?: false // true if update was successful, false otherwise

    /** Delete by name */
    fun deleteByName(name: String): Boolean =
        minerals.removeIf { (it.name ?: "").equals(name, ignoreCase = true) }

    /** Sort alphabetically by name (unknown names go last). */
    fun sortByName(): List<Mineral> =
        minerals.sortedBy { it.name?.lowercase() ?: "~" }

    /** Search by name (case-insensitive).
     *  Returns all minerals whose name starts with the given query.
     *  Example: "Am" -> Amethyst, Amazonite, ...
     */
    fun searchByName(name: String): List<Mineral> {
        if (name.isBlank()) {
            return emptyList()
        }
        else {
            return minerals.filter { it.name?.startsWith(name, ignoreCase = true) == true }
        }
    }
    /**
     * Filter by optional criteria.
     * Any null/blank parameter is ignored.
     * - nameContains: substring in name (case-insensitive)
     * - color: exact color match against any color in list (case-insensitive)
     * - fracture: exact match (case-insensitive)
     * - hardnessValue: matches if value ∈ [hardnessMin, hardnessMax]
     */
    fun filter(
        nameContains: String? = null,
        color: String? = null,
        fracture: String? = null,
        hardnessValue: Double? = null
    ): List<Mineral> = minerals.filter { m ->
        val nameOk = nameContains.isNullOrBlank() || (m.name ?: "").contains(nameContains, true)
        val colorOk = color.isNullOrBlank() || m.color.any { it.equals(color, true) }
        val fracOk  = fracture.isNullOrBlank() || (m.fracture ?: "").equals(fracture, true)
        val hardOk  = hardnessValue == null ||
                (m.hardnessMin != null && m.hardnessMax != null &&
                        hardnessValue in m.hardnessMin!!..m.hardnessMax!!)
        nameOk && colorOk && fracOk && hardOk
    }
}