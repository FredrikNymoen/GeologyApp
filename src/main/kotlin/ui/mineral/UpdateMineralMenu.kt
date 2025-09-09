package org.example.ui.mineral

import org.example.models.Mineral
import org.example.services.MineralService
import org.example.utils.Input

/**
 * Separate update flow for a single Mineral.
 */
class UpdateMineralMenu(private val service: MineralService) {

    fun run() {
        outer@ while (true) {
            val list = service.listAll()
            if (list.isEmpty()) { println("No minerals to update."); return }

            val items = list.mapIndexed { i, m -> "${i + 1} - ${m.name ?: "(unknown)"}" } + "B - Back"
            Input.showMenu("Update Mineral", items)
            val pick = Input.choice()
            if (pick.equals("b", true)) return

            val idx = pick.toIntOrNull()?.minus(1)
            if (idx == null || idx !in list.indices) {
                println("Invalid choice.");
                continue@outer
            }
            val current = list[idx]

            fun parseList(input: String) =
                input.split(Regex("[,/]")).map { it.trim() }.filter { it.isNotEmpty() }

            fun ask(label: String, hint: String): String {
                print("$label [$hint]: ")
                return Input.choice()
            }

            edit@ while (true) {
                println("\nCurrent:")
                println(current) // uses Mineral.toString()

                println("\nEnter new values (blank = keep, '-' = clear text fields):")
                val nameIn     = ask("New name", current.name ?: "keep")
                val lusterIn   = ask("New luster (comma or '/' separated)", current.luster.joinToString())
                val colorIn    = ask("New color  (comma or '/' separated)", current.color.joinToString())
                val minIn      = ask("New hardness MIN (1..10)", current.hardnessMin?.toString() ?: "keep")
                val maxIn      = ask("New hardness MAX (1..10)", current.hardnessMax?.toString() ?: "keep")
                val fractureIn = ask("New fracture", current.fracture ?: "keep")

                val proposedName = when {
                    nameIn == "-"    -> null
                    nameIn.isBlank()  -> current.name
                    else              -> nameIn
                }
                val proposedFracture = when {
                    fractureIn == "-" -> null
                    fractureIn.isBlank()-> current.fracture
                    else               -> fractureIn
                }
                val proposedLuster = if (lusterIn.isBlank()) {
                    current.luster
                } else {
                    parseList(lusterIn)
                }
                val proposedColor  = if (colorIn.isBlank()) {
                    current.color
                }  else {
                    parseList(colorIn)
                }

                val newMin = if (minIn.isBlank()) current.hardnessMin else minIn.toDoubleOrNull()
                val newMax = if (maxIn.isBlank()) current.hardnessMax else maxIn.toDoubleOrNull()
                if ((newMin != null && newMin !in 1.0..10.0) ||
                    (newMax != null && newMax !in 1.0..10.0)) {
                    println("Invalid hardness. Enter a number between 1 and 10."); continue@edit
                }

                var pMin = newMin; var pMax = newMax
                if (pMin != null && pMax != null && pMin > pMax) {
                    println("Note: MIN > MAX; swapping."); val t = pMin; pMin = pMax; pMax = t
                }


                println("\n--- Preview ---")
                // Build a preview WITHOUT mutating current
                val preview = Mineral(
                    name = proposedName,
                    luster = proposedLuster,
                    color = proposedColor,
                    hardnessMin = pMin,
                    hardnessMax = pMax,
                    fracture = proposedFracture
                )
                println(preview)

                print("\nSave changes? [y]es / [e]dit again / [c]ancel: ")
                when (Input.choice().lowercase()) {
                    "y", "yes" -> {
                        val ok = service.update(idx) { m ->
                            m.name = proposedName
                            m.luster = proposedLuster
                            m.color = proposedColor
                            m.hardnessMin = pMin
                            m.hardnessMax = pMax
                            m.fracture = proposedFracture
                        }
                        if (ok) println("\nUpdated successfully:\n${service.listAll()[idx]}") else println("Update failed.")
                        return
                    }
                    "e", "edit"   -> continue@edit
                    "c", "cancel" -> { println("Update canceled."); return }
                    else          -> println("Please answer y/e/c.")
                }
            }
        }
    }
}
