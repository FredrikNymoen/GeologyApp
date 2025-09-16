package org.example.ui.mineral

import org.example.actions.mineral.MineralMenuAction
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput
import kotlin.io.println

class MineralMenu(
    val mineralService: MineralService
){
    private val addMineralMenu = AddMineralMenu(mineralService)
    private val updateMineralMenu = UpdateMineralMenu(mineralService)
    private val filterMineralMenu = FilterMineralMenu(mineralService)
    private val options = MineralMenuAction.entries.map { "${it.shortcut} - ${it.label}" }

    fun run() {
        while (true) {
            // Show menu and read choice
            ConsoleIO.showMenu("Mineral Menu", options)
            val choice = ConsoleIO.choice()

            val action = fromInput<MineralMenuAction>(choice)
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            when (action) {
                // 1) List all
                MineralMenuAction.ListAll -> {
                    val all = mineralService.getAll()
                    if (all.isEmpty()) println("No minerals found.")
                    else all.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Mineral.toString()
                }

                // 2) Sort A–Z
                MineralMenuAction.SortAZ -> {
                    val sorted = mineralService.sortByName()
                    if (sorted.isEmpty()) println("No minerals found.")
                    else sorted.forEachIndexed { i, m -> println("${i + 1}. $m") }
                }

                // 3) Search by exact name
                MineralMenuAction.SearchByName -> {
                    print("Name to search: ")
                    val q = ConsoleIO.choice()
                    val hits = mineralService.getByName(q)
                    if (hits.isEmpty()) {
                        println("No matches.")
                    } else {
                        hits.forEachIndexed { i, m ->
                            println("${i + 1}. $m")   // uses your Mineral.toString()
                        }
                    }
                }

                // 4) Filter (blank = ignore)
                MineralMenuAction.Filter -> {
                    filterMineralMenu.run()
                }

                // 5) Add mineral
                MineralMenuAction.Add -> {
                    addMineralMenu.run()
                }

                // 6) Update mineral (opens the dedicated update flow)
                MineralMenuAction.Update -> {
                    updateMineralMenu.run()
                }

                // 7) Delete mineral by name
                MineralMenuAction.Delete -> {
                    print("Name to delete: ")
                    val q = ConsoleIO.choice()
                    val removed = mineralService.delete(q)
                    println(if (removed) "Deleted '$q'." else "No mineral found with name '$q'.")
                }

                // 8) Back
                MineralMenuAction.Back -> return
            }
        }
    }




}