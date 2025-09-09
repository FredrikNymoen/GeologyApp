package org.example.ui

import org.example.services.MineralService
import org.example.utils.Input

class MineralMenu(
    val mineralService: MineralService
){
    val mineralMenuList = listOf(
        "1 - List all minerals",
        "2 - Add mineral",
        "3 - Update mineral",
        "4 - Delete mineral",
        "5 - Return to main menu"
    )

    fun run() {
        while (true) {
            Input.showMenu("Mineral Menu", mineralMenuList)
            when (Input.choice()) {
                "1" -> {mineralService.getMineralList().forEach {mineral ->
                    println(
                        "Name: ${mineral.name ?: "(unknown)"} | " +
                        "Luster: ${mineral.luster.joinToString()} | " +
                        "Color: ${mineral.color.joinToString()} | " +
                        "Hardness: ${mineral.hardnessMin ?: "?"}–${mineral.hardnessMax ?: "?"} | " +
                        "Fracture: ${mineral.fracture ?: "(unknown)"}"
                    )
                }}
                "2" -> {}
                "3" -> { /* prompt filters and call service.filter(...) */ }
                "4" -> {}
                "5" -> return
                else -> println("Invalid choice")
            }
        }
    }
}