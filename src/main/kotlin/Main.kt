package org.example

import org.example.services.LocationService
import org.example.services.MineralService
import org.example.ui.location.LocationMenu
import org.example.ui.MainMenu
import org.example.ui.MenuAction
import org.example.ui.mineral.MineralMenu

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
/**
 * Main entry point to the application
 * Creates services and menus, then runs the main loop
 */
fun main() {
    val mineralService = MineralService()
    val locationService = LocationService()

    val mainMenu = MainMenu()
    val mineralMenu = MineralMenu(mineralService)
    val locationMenu = LocationMenu(locationService)

    /**
     * Main loop
     * Show main menu and call sub-menus or exit based on user choice
     */
    while (true) {
        when (mainMenu.run()){
            MenuAction.Locations -> locationMenu.run()
            MenuAction.Minerals -> mineralMenu.run()
            MenuAction.Workers -> println("Workers")
            MenuAction.UpdateWorkers -> println("Update workers")
            MenuAction.Exit -> return
        }
    }
}