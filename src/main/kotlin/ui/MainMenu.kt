package org.example.ui

import org.example.utils.Input

class MainMenu{
    val mainMenuList = listOf(
        "1 - See geological location data",
        "2 - See mineral data",
        "3 - See worker data",
        "4 - Update worker data",
        "5 - Shut down application"
    )

    fun run(): MenuAction {
        Input.showMenu("Main Menu", mainMenuList)
        return when (Input.choice()) {
            "1" -> MenuAction.Locations
            "2" -> MenuAction.Minerals
            "3" -> MenuAction.Workers
            "4" -> MenuAction.UpdateWorkers
            "5" -> MenuAction.Exit
            else -> { println("Invalid choice"); run() }
        }
    }

}