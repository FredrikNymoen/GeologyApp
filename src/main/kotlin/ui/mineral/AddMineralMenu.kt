package org.example.ui.mineral

import org.example.models.Mineral
import org.example.services.MineralService
import org.example.utils.Input

/**
 * Handles adding a new Mineral with validation and confirmation.
 * Keeps asking until user confirms (save) or cancels.
 */
class AddMineralMenu(private val service: MineralService) {

    private fun parseList(input: String): List<String> =
        input.split(Regex("[,/]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    fun run() {
        add@ while (true) {
            println("\n=== Add Mineral ===")
            println("Leave a field blank if unknown.")

            fun ask(label: String): String {
                print("$label: ")
                return Input.choice()
            }

            val nameIn     = ask("Name").ifBlank { null }
            val lusterIn   = ask("Luster (comma or '/' separated)")
            val colorIn    = ask("Color  (comma or '/' separated)")
            val minIn      = ask("Hardness MIN (1..10)")
            val maxIn      = ask("Hardness MAX (1..10)")
            val fractureIn = ask("Fracture").ifBlank { null }

            val lusterList = if (lusterIn.isBlank()) emptyList() else parseList(lusterIn)
            val colorList  = if (colorIn.isBlank())  emptyList() else parseList(colorIn)

            val minVal = minIn.toDoubleOrNull()
            val maxVal = maxIn.toDoubleOrNull()

            // Validate Mohs inputs if provided
            if ((minVal != null && minVal !in 1.0..10.0) ||
                (maxVal != null && maxVal !in 1.0..10.0)) {
                println("Invalid hardness. Values must be between 1 and 10.")
                continue@add
            }

            var hMin = minVal
            var hMax = maxVal
            if (hMin != null && hMax != null && hMin > hMax) {
                println("Note: MIN > MAX; swapping.")
                val t = hMin; hMin = hMax; hMax = t
            }



            println("\n--- Preview ---")
            val candidate = Mineral(
                name = nameIn,
                luster = lusterList,
                color = colorList,
                hardnessMin = hMin,
                hardnessMax = hMax,
                fracture = fractureIn
            )
            println(candidate) // uses your Mineral.toString()

            print("\nSave this mineral? [y]es / [e]dit again / [c]ancel: ")
            when (Input.choice().lowercase()) {
                "y", "yes" -> {
                    service.add(candidate)
                    println("Added:\n$candidate")
                    return
                }
                "e", "edit" -> continue@add
                "c", "cancel" -> {
                    println("Add canceled.")
                    return
                }
                else -> println("Please answer y/e/c.")
            }
        }
    }
}
