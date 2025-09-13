package org.example.ui.mineral

import org.example.actions.FilterMenuAction
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

class FilterMineralMenu(private val service: MineralService) {

    fun run() {
        // State for filters (nullable means "ignore")
        var nameContains: String? = null
        var color: String? = null
        var fracture: String? = null
        var hardness: Double? = null


        val options = FilterMenuAction.entries.map { "${it.shortcut} - ${it.label}" }
        loop@ while (true) {
            // Show current filter state
            println("\n=== Filter Minerals ===")
            println("Current filters -> " +
                    "nameContains=${nameContains ?: "-"}, " +
                    "color=${color ?: "-"}, " +
                    "fracture=${fracture ?: "-"}, " +
                    "hardness=${hardness ?: "-"}")

            // Generate menu lines directly from the enum
            ConsoleIO.showMenu("Filter Menu", options)

            // Read user choice and convert to enum action
            val choice = ConsoleIO.choice().trim()
            val action = fromInput<FilterMenuAction>(choice)
            if (action == null) {
                println("Invalid choice.")
                continue@loop
            }

            // Handle user action
            when (action) {
                FilterMenuAction.SetName -> {
                    print("Name contains (blank=ignore): ")
                    nameContains = ConsoleIO.choice().ifBlank { null }
                }
                FilterMenuAction.SetColor -> {
                    print("Color (exact, blank=ignore): ")
                    color = ConsoleIO.choice().ifBlank { null }
                }
                FilterMenuAction.SetFracture -> {
                    print("Fracture (exact, blank=ignore): ")
                    fracture = ConsoleIO.choice().ifBlank { null }
                }
                FilterMenuAction.SetHardness -> {
                    print("Hardness value (blank=ignore): ")
                    hardness = ConsoleIO.choice().toDoubleOrNull()
                }
                FilterMenuAction.RunFilter -> {
                    // Call service with current filters
                    val results = service.filter(
                        nameContains = nameContains,
                        color = color,
                        fracture = fracture,
                        hardnessValue = hardness
                    )
                    if (results.isEmpty()) println("No matches.")
                    else results.forEachIndexed { i, m -> println("${i + 1}. $m") }

                    // After showing results, let the user refine or exit
                    print("\nRefine filters? [y/yes = yes, anything else = no]: ")
                    when (ConsoleIO.choice()) {
                        "y", "yes" -> continue@loop
                        else -> return
                    }
                }
                FilterMenuAction.Clear -> {
                    // Reset all filters
                    nameContains = null
                    color = null
                    fracture = null
                    hardness = null
                    println("Filters cleared.")
                }
                FilterMenuAction.Back -> return
            }
        }
    }
}
