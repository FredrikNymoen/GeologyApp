package org.example.ui.mineral

import org.example.services.MineralService
import org.example.utils.Input
import kotlin.io.println

class MineralMenu(
    val mineralService: MineralService
){
    private val mineralMenuList = listOf(
        "1 - List all minerals",
        "2 - Sort all minerals A-Z",
        "3 - Search for mineral by name",
        "4 - Filter minerals",
        "5 - Add mineral",
        "6 - Update mineral",
        "7 - Delete mineral",
        "8 - Return to main menu",
    )


    fun run() {
        while (true) {
            // Show menu and read choice
            Input.showMenu("Mineral Menu", mineralMenuList)
            when (Input.choice()) {
                // 1) List all
                "1" -> {
                    val all = mineralService.listAll()
                    if (all.isEmpty()) println("No minerals found.")
                    else all.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Mineral.toString()
                }

                // 2) Sort A–Z
                "2" -> {
                    val sorted = mineralService.sortByName()
                    if (sorted.isEmpty()) println("No minerals found.")
                    else sorted.forEachIndexed { i, m -> println("${i + 1}. $m") }
                }

                // 3) Search by exact name
                "3" -> {
                    print("Name to search: ")
                    val q = Input.choice()
                    val hits = mineralService.searchByName(q)
                    if (hits.isEmpty()) {
                        println("No matches.")
                    } else {
                        hits.forEachIndexed { i, m ->
                            println("${i + 1}. $m")   // uses your Mineral.toString()
                        }
                    }
                }

                // 4) Filter (blank = ignore)
                "4" -> {
                    FilterMineralMenu(mineralService).run()
                }

                // 5) Add mineral
                "5" -> {
                    AddMineralMenu(mineralService).run()
                }

                // 6) Update mineral (opens the dedicated update flow)
                "6" -> {
                    UpdateMineralMenu(mineralService).run()
                }

                // 7) Delete mineral by name
                "7" -> {
                    print("Name to delete: ")
                    val q = Input.choice()
                    val removed = mineralService.deleteByName(q)
                    println(if (removed) "Deleted '$q'." else "No mineral found with name '$q'.")
                }

                // 8) Back
                "8" -> return

                else -> println("Invalid choice. Please try again.")
            }
        }
    }




}