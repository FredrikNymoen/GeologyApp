package org.example.services

import org.example.models.Mineral
import org.example.utils.MineralLoader

/**
 * Service class to manage the list of minerals.
 * Loads the minerals from file on initialization.
 * Provides CRUD operations and filtering/sorting capabilities.
 */
class MineralService {

    private val minerals = mutableListOf<Mineral>() //The list with all the minerals

    init {
        minerals += MineralLoader.loadFromFile()          // update minerals when creating the object
    }

    /** Returns all minerals as a list. */
    fun getAll() : List<Mineral> {
        return minerals.toList() // Returns the minerals as a list
    }

    /** Check if a mineral with the given name exists (case-insensitive). */
    fun exists(name: String) : Boolean {
        return minerals.any { (it.name ?: "").equals(name, ignoreCase = true) }
    }

    /** Add a new mineral to the list. */
    fun add(mineral: Mineral){
        minerals.add(mineral)
    }

    /** Update an existing mineral by index, applying the given mutation function.
     *  Returns true if update was successful, false if index was invalid.
     */
    fun update(index: Int, mutate: (Mineral) -> Unit): Boolean =
        minerals.getOrNull(index)?.let { mutate(it); true } ?: false // true if update was successful, false otherwise

    /** Delete by name */
    fun delete(name: String): Boolean =
        minerals.removeIf { (it.name ?: "").equals(name, ignoreCase = true) }

    /** Sort alphabetically by name (unknown names go last). */
    fun sortByName(): List<Mineral> =
        minerals.sortedBy { it.name?.lowercase() ?: "~" }

    /** Search by name (case-insensitive).
     *  Returns all minerals whose name starts with the given query.
     *  Example: "Am" -> Amethyst, Amazonite, ...
     */
    fun getByName(name: String): List<Mineral> {
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

        val hardOk = when {
            hardnessValue == null -> true
            m.hardnessMin != null && m.hardnessMax != null ->
                hardnessValue in m.hardnessMin!!..m.hardnessMax!!
            m.hardnessMin != null -> hardnessValue >= m.hardnessMin!!
            m.hardnessMax != null -> hardnessValue <= m.hardnessMax!!
            else -> true // no hardness info at all
        }

        nameOk && colorOk && fracOk && hardOk
    }
}