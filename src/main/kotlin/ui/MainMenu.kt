package org.example.ui

import org.example.actions.MainMenuAction
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

object MainMenu{

    fun run(): MainMenuAction {
        val options = MainMenuAction.entries.map { "${it.shortcut} - ${it.label}" }
        while (true) {
            ConsoleIO.showMenu("Main Menu", options)

            val choice = ConsoleIO.choice()
            val action = fromInput<MainMenuAction>(choice)

            if (action != null) return action
            println("Invalid choice, try again.")
        }
    }

}