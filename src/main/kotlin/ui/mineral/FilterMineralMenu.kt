package org.example.ui.mineral

import org.example.services.MineralService
import org.example.utils.Input

/**
 * Interactive filtering: lets you refine criteria until you're done.
 * Blank input = ignore that criterion.
 */
class FilterMineralMenu(private val service: MineralService) {

    fun run() {
        var nameContains: String? = null
        var color: String? = null
        var fracture: String? = null
        var hardness: Double? = null

        loop@ while (true) {
            println("\n=== Filter Minerals ===")
            println("Current filters -> " +
                    "nameContains=${nameContains ?: "-"}, " +
                    "color=${color ?: "-"}, " +
                    "fracture=${fracture ?: "-"}, " +
                    "hardness=${hardness ?: "-"}")

            println(
                "1 - Set nameContains\n" +
                        "2 - Set color (exact)\n" +
                        "3 - Set fracture (exact)\n" +
                        "4 - Set hardness value (e.g. 6.8)\n" +
                        "5 - Run filter\n" +
                        "6 - Clear all filters\n" +
                        "7 - Back"
            )
            print("Choose: ")
            when (Input.choice()) {
                "1" -> { print("Name contains (blank=ignore): "); nameContains = Input.choice().ifBlank { null } }
                "2" -> { print("Color (exact, blank=ignore): "); color = Input.choice().ifBlank { null } }
                "3" -> { print("Fracture (exact, blank=ignore): "); fracture = Input.choice().ifBlank { null } }
                "4" -> {
                    print("Hardness value (blank=ignore): ")
                    hardness = Input.choice().toDoubleOrNull()
                }
                "5" -> {
                    val results = service.filter(
                        nameContains = nameContains,
                        color = color,
                        fracture = fracture,
                        hardnessValue = hardness
                    )
                    if (results.isEmpty()) println("No matches.")
                    else results.forEachIndexed { i, m -> println("${i + 1}. $m") }

                    // After showing results, ask to refine or go back
                    print("\nRefine filters? [y]es / [n]o: ")
                    when (Input.choice().lowercase()) {
                        "y", "yes" -> continue@loop
                        else -> return
                    }
                }
                "6" -> { nameContains = null; color = null; fracture = null; hardness = null; println("Filters cleared.") }
                "7" -> return
                else -> println("Invalid choice.")
            }
        }
    }
}
