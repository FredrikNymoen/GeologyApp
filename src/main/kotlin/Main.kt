package org.example

import org.example.services.LocationService
import org.example.services.MineralService
import org.example.ui.location.LocationMenu
import org.example.ui.MainMenu
import org.example.actions.MainMenuAction
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

    val mineralMenu = MineralMenu(mineralService)
    val locationMenu = LocationMenu(locationService)

    /**
     * Main loop
     * Show main menu and call sub-menus or exit based on user choice
     */
    while (true) {
        when (MainMenu.run()){
            MainMenuAction.Locations -> locationMenu.run()
            MainMenuAction.Minerals -> mineralMenu.run()
            MainMenuAction.Workers -> println("Workers")
            MainMenuAction.UpdateWorkers -> println("Update workers")
            MainMenuAction.Exit -> return
        }
    }
}