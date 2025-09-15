package org.example.ui.mineral

import org.example.actions.ConfirmationAction
import org.example.models.Mineral
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO

/**
 * Handles adding a new Mineral with validation and confirmation.
 * Keeps asking until user confirms (save) or cancels.
 * Optional: run a callback after saving (e.g., link mineral to a location).
 */
class AddMineralMenu(
    private val service: MineralService,
    private val afterSave: ((Mineral) -> Unit)? = null
) {

    // Helper to parse comma- or slash-separated lists
    private fun parseList(input: String): List<String> =
        input.split(Regex("[,/]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    fun run() {
        while (true) {
            val candidate = buildMineral() ?: continue
            if (confirmAndSave(candidate)) return
        }
    }

    // Collect and validate user input, then build a Mineral candidate.
// Returns null if input was invalid (so loop can continue).
    private fun buildMineral(): Mineral? {
        println("\n=== Add Mineral ===")
        println("Leave a field blank if unknown.")

        fun ask(label: String): String {
            print("$label: ")
            return ConsoleIO.choice()
        }

        val nameIn     = ask("Name").ifBlank { null }
        val lusterIn   = ask("Luster (comma or '/' separated)")
        val colorIn    = ask("Color  (comma or '/' separated)")
        val minIn      = ask("Hardness MIN (1..10)")
        val maxIn      = ask("Hardness MAX (1..10)")
        val fractureIn = ask("Fracture").ifBlank { null }

        val lusterList = if (lusterIn.isBlank()) emptyList() else parseList(lusterIn)
        val colorList  = if (colorIn.isBlank()) emptyList() else parseList(colorIn)

        val (hMin, hMax) = validateHardness(minIn, maxIn) ?: return null

        return Mineral(
            name = nameIn,
            luster = lusterList,
            color = colorList,
            hardnessMin = hMin,
            hardnessMax = hMax,
            fracture = fractureIn
        )
    }

    // Validate and normalize Mohs hardness values
    private fun validateHardness(minIn: String, maxIn: String): Pair<Double?, Double?>? {
        val minVal = minIn.toDoubleOrNull()
        val maxVal = maxIn.toDoubleOrNull()

        if ((minVal != null && minVal !in 1.0..10.0) ||
            (maxVal != null && maxVal !in 1.0..10.0)) {
            println("Invalid hardness. Values must be between 1 and 10.")
            return null
        }

        var hMin = minVal
        var hMax = maxVal
        if (hMin != null && hMax != null && hMin > hMax) {
            println("Note: MIN > MAX; swapping.")
            val t = hMin; hMin = hMax; hMax = t
        }
        return hMin to hMax
    }

    // Ask for confirmation (y/e/c) and handle saving
    private fun confirmAndSave(candidate: Mineral): Boolean {
        println("\n--- Preview ---")
        println(candidate)

        print("\nSave this mineral? [y]es / [e]dit again / [c]ancel: ")
        return when (ConfirmationAction.from(ConsoleIO.choice())) {
            ConfirmationAction.Save -> {
                service.add(candidate)

                // IMPORTANT: invoke the callback so AddMineralToLocationMenu can link it
                afterSave?.invoke(candidate)

                println("Added:\n$candidate")
                true
            }
            ConfirmationAction.Edit -> false
            ConfirmationAction.Cancel -> {
                println("Add canceled.")
                true
            }
            null -> {
                println("Please answer y/e/c.")
                false
            }
        }
    }


}
