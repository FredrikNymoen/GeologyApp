package org.example.ui.mineral

import org.example.actions.ConfirmationAction
import org.example.models.Mineral
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO

/**
 * Update flow for an existing [Mineral]: select, edit, preview, confirm, save.
 * Blank keeps current; "-" clears text. Ensures unique name and valid hardness.
 */
class UpdateMineralMenu(private val mineralService: MineralService) {

    /** Starts the update mineral flow. */
    fun run() {
        val list = mineralService.getAll()
        if (list.isEmpty()) { println("No minerals to update."); return }

        val idx = selectMineralIndex(list) ?: return
        editLoop(idx)
    }

    // ---------------- selection ----------------

    /** Shows a menu of minerals to select from; returns the selected index or null if canceled. */
    private fun selectMineralIndex(list: List<Mineral>): Int? {
        val items = list.mapIndexed { i, m -> "${i + 1} - ${m.name ?: "(unknown)"}" } + "B - Go back"
        while (true) {
            ConsoleIO.showMenu("Update Mineral", items)
            val pick = ConsoleIO.choice()
            if (pick.equals("b", true)) return null
            val idx = pick.toIntOrNull()?.minus(1)
            if (idx != null && idx in list.indices) return idx
            println("Invalid choice.")
        }
    }

    // ---------------- edit loop ----------------

    /** Main edit loop: show current, collect proposal, validate, preview, confirm, save. */
    private fun editLoop(index: Int) {
        while (true) {
            val current = mineralService.getAll().getOrNull(index) ?: run {
                println("Mineral no longer available."); return
            }
            println("\nCurrent:\n$current")

            val proposal = collectProposal(current) ?: run {
                println("Update canceled."); return
            }

            // Unique name (allow unchanged)
            proposal.name?.let { newName ->
                val same = current.name.orEmpty().equals(newName, ignoreCase = true)
                if (!same && mineralService.exists(newName)) {
                    println("A mineral named '$newName' already exists. Choose another name.")
                    continue
                }
            }

            val preview = buildPreview(proposal)
            println("\n--- Preview ---\n$preview")

            print("\nSave this mineral? [y]es / [e]dit again / [c]ancel: ")
            when (ConfirmationAction.from(ConsoleIO.choice())) {
                ConfirmationAction.Save -> { save(index, proposal); return }
                ConfirmationAction.Edit -> continue
                ConfirmationAction.Cancel -> { println("Update canceled."); return }
                null -> println("Please answer y/e/c.")
            }
        }
    }

    // ---------------- proposal ----------------

    // Proposal data collected from user before validation
    private data class Proposal(
        val name: String?,
        val luster: List<String>,
        val color: List<String>,
        val hardMin: Double?,
        val hardMax: Double?,
        val fracture: String?
    )

    /** Collects new values from user; blank keeps current, "-" clears text fields.
     *  Returns null if user input is invalid (e.g. hardness out of range).
     */
    private fun collectProposal(current: Mineral): Proposal? {
        println("\nEnter new values (blank = keep, '-' = clear text fields):")

        val nameIn     = ConsoleIO.prompt("New name [${current.name ?: "keep"}]")
        val lusterIn   = ConsoleIO.prompt("New luster (comma or '/' separated) [${current.luster.joinToString()}]")
        val colorIn    = ConsoleIO.prompt("New color  (comma or '/' separated) [${current.color.joinToString()}]")
        val minIn      = ConsoleIO.prompt("New hardness MIN (1..10) [${current.hardnessMin ?: "keep"}]")
        val maxIn      = ConsoleIO.prompt("New hardness MAX (1..10) [${current.hardnessMax ?: "keep"}]")
        val fractureIn = ConsoleIO.prompt("New fracture [${current.fracture ?: "keep"}]")

        val name      = when { nameIn == "-" -> null; nameIn.isBlank() -> current.name; else -> nameIn }
        val fracture  = when { fractureIn == "-" -> null; fractureIn.isBlank() -> current.fracture; else -> fractureIn }
        val luster    = if (lusterIn.isBlank()) current.luster else ConsoleIO.parseList(lusterIn)
        val color     = if (colorIn.isBlank())  current.color  else ConsoleIO.parseList(colorIn)

        val (minOk, maxOk) = parseHardness(minIn, maxIn, current.hardnessMin, current.hardnessMax) ?: return null

        return Proposal(name, luster, color, minOk, maxOk, fracture)
    }

    // ---------------- validation ----------------

    /** Parses and validates hardness input.
     *  Returns Pair(min, max) or null if invalid input.
     */
    private fun parseHardness(
        minIn: String,
        maxIn: String,
        curMin: Double?,
        curMax: Double?
    ): Pair<Double?, Double?>? {
        val minVal = if (minIn.isBlank()) curMin else minIn.toDoubleOrNull()
        val maxVal = if (maxIn.isBlank()) curMax else maxIn.toDoubleOrNull()

        if ((minVal != null && minVal !in 1.0..10.0) || (maxVal != null && maxVal !in 1.0..10.0)) {
            println("Invalid hardness. Enter a number between 1 and 10.")
            return null
        }

        var a = minVal; var b = maxVal
        if (a != null && b != null && a > b) {
            println("Note: MIN > MAX; swapping.")
            val t = a; a = b; b = t
        }
        return a to b
    }

    // ---------------- preview & persist ----------------

    /** Builds a Mineral from the proposal for previewing. */
    private fun buildPreview(p: Proposal) = Mineral(
        name        = p.name,
        luster      = p.luster,
        color       = p.color,
        hardnessMin = p.hardMin,
        hardnessMax = p.hardMax,
        fracture    = p.fracture
    )

    /** Saves the proposal to the mineral at [index]. */
    private fun save(index: Int, p: Proposal) {
        val ok = mineralService.update(index) { m ->
            m.name        = p.name
            m.luster      = p.luster
            m.color       = p.color
            m.hardnessMin = p.hardMin
            m.hardnessMax = p.hardMax
            m.fracture    = p.fracture
        }
        if (ok) println("\nUpdated successfully:\n${mineralService.getAll()[index]}") else println("Update failed.")
    }
}
