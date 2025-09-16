package org.example.ui.mineral

import org.example.actions.ConfirmationAction
import org.example.actions.mineral.AddMineralMenuAction
import org.example.models.Mineral
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

/**
 * Handles adding a new Mineral with validation and confirmation.
 * Keeps asking until user confirms (save) or cancels.
 * Optional: run a callback after saving (e.g., link mineral to a location).
 */
class AddMineralMenu(
    private val service: MineralService,
    private val afterSave: ((Mineral) -> Unit)? = null, // callback after saving
    private val allowSelectExisting: Boolean = false
) {


    private val options = AddMineralMenuAction.entries.map { "${it.shortcut} - ${it.label}" }
    fun run() {
        // If enabled, offer to select an existing mineral first.
        if (allowSelectExisting) {
            when (promptMode()) {
                AddMineralMenuAction.SelectExisting -> {
                    val selected = selectExisting() ?: return
                    // Do NOT add to service; it already exists. Just notify caller (e.g., link to location).
                    afterSave?.invoke(selected)
                    println("Selected existing:\n$selected")
                    return
                }
                AddMineralMenuAction.CreateNew -> {
                    // fall through to creation loop
                }
                AddMineralMenuAction.Cancel -> {
                    println("Canceled.")
                    return
                }
            }
        }

        while (true) {
            val candidate = buildMineral() ?: continue
            if (confirmAndSave(candidate)) return
        }
    }

    /** Ask the user which mode they want. */
    private fun promptMode(): AddMineralMenuAction {
        while (true) {
            ConsoleIO.showMenu("Add/Select Mineral", options)
            val choice = ConsoleIO.choice()
            val action = fromInput<AddMineralMenuAction>(choice)
            if (action != null) return action
            println("Invalid choice. Please try again.")
        }
    }



    // Collect and validate user input, then build a Mineral candidate.
// Returns null if input was invalid (so loop can continue).
    private fun buildMineral(): Mineral? {
        println("\n=== Add Mineral ===")
        println("Leave a field blank if unknown (except Name).")

        fun ask(label: String): String {
            print("$label: ")
            return ConsoleIO.choice()
        }

        val nameIn: String = run {
            while (true) {
                val n = ConsoleIO.nonEmpty("Name").trim()
                if (service.exists(n)) {
                    println("A mineral named '$n' already exists. Choose another name.")
                    continue
                }
                return@run n
            }
            // unreachable
            ""
        }

        val lusterIn   = ask("Luster (comma or '/' separated)")
        val colorIn    = ask("Color  (comma or '/' separated)")
        val minIn      = ask("Hardness MIN (1..10)")
        val maxIn      = ask("Hardness MAX (1..10)")
        val fractureIn = ask("Fracture").ifBlank { null }

        val lusterList = if (lusterIn.isBlank()) emptyList() else ConsoleIO.parseList(lusterIn)
        val colorList  = if (colorIn.isBlank()) emptyList() else ConsoleIO.parseList(colorIn)

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

    /** Let the user select an existing mineral by index or by exact name (case-insensitive). */
    private fun selectExisting(): Mineral? {
        val all = service.getAll()
        if (all.isEmpty()) {
            println("No minerals in the catalog yet.")
            return null
        }

        println("\n--- Existing minerals ---")
        all.forEachIndexed { i, m ->
            val label = m.name ?: "(unknown)"
            println("${i + 1}. $label")
        }
        println("-------------------------")

        val raw = ConsoleIO.prompt("Enter number or name (blank to cancel)")
        if (raw.isBlank()) {
            println("Canceled.")
            return null
        }

        // Try by number
        val idx = raw.toIntOrNull()
        if (idx != null && idx in 1..all.size) {
            return all[idx - 1]
        }

        // Try by exact name (ignore case)
        val byName = all.firstOrNull { it.name?.equals(raw, ignoreCase = true) == true }
        if (byName != null) return byName

        println("Not found: '$raw'.")
        return null
    }


}
