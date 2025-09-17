package org.example.ui.mineral

import org.example.actions.mineral.MineralMenuAction
import org.example.models.Mineral
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput
import kotlin.io.println

/**
 * Menu to manage minerals.
 * Options to list all, sort A-Z, search by name, filter, add, update, delete.
 * Uses MineralService for data operations.
 */
class MineralMenu(
    private val mineralService: MineralService
) {
    private val addMineralMenu = AddMineralMenu(mineralService)
    private val updateMineralMenu = UpdateMineralMenu(mineralService)
    private val filterMineralMenu = FilterMineralMenu(mineralService)
    private val options = MineralMenuAction.entries.map { "${it.shortcut} - ${it.label}" }


    // Map actions to handler functions
    private val handlers: Map<MineralMenuAction, () -> Unit> = mapOf(
        MineralMenuAction.ListAll   to ::handleListAll,
        MineralMenuAction.SortAZ    to ::handleSortAZ,
        MineralMenuAction.SearchByName to ::handleSearchByName,
        MineralMenuAction.Filter    to ::handleFilter,
        MineralMenuAction.Add       to ::handleAdd,
        MineralMenuAction.Update    to ::handleUpdate,
        MineralMenuAction.Delete    to ::handleDelete
    )

    /** Main loop; returns when user chooses Back. */
    fun run() {
        while (true) {
            ConsoleIO.showMenu("Mineral Menu", options)

            val action = fromInput<MineralMenuAction>(ConsoleIO.choice())
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            if (action == MineralMenuAction.Back) return

            handlers[action]?.invoke() ?: println("Invalid choice. Please try again.")
        }
    }


    /** list all minerals, or show message if none exist */
    private fun handleListAll() =
        printIndexed(mineralService.getAll(), emptyMessage = "No minerals found.")

    /** list all minerals sorted A-Z, or show message if none exist */
    private fun handleSortAZ() =
        printIndexed(mineralService.sortByName(), emptyMessage = "No minerals found.")

    /** prompt for name, search, and print results or no-match message */
    private fun handleSearchByName() {
        print("Name to search: ")
        val q = ConsoleIO.choice()
        printIndexed(mineralService.getByName(q), emptyMessage = "No matches.")
    }

    /** invoke the filter submenu */
    private fun handleFilter() = filterMineralMenu.run()
    /** invoke the add submenu */
    private fun handleAdd() = addMineralMenu.run()
    /** invoke the update submenu */
    private fun handleUpdate() = updateMineralMenu.run()

    /** prompt for name, attempt delete, and print success/failure message */
    private fun handleDelete() {
        print("Name to delete: ")
        val q = ConsoleIO.choice()
        val removed = mineralService.delete(q)
        println(if (removed) "Deleted '$q'." else "No mineral found with name '$q'.")
    }

    /** Prints indexed list of minerals or empty message if list is empty. */
    private fun printIndexed(items: List<Mineral>, emptyMessage: String) {
        if (items.isEmpty()) {
            println(emptyMessage)
        } else {
            items.forEachIndexed { i, m -> println("${i + 1}. $m") }
        }
    }
}
