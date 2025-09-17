package org.example.ui

import org.example.actions.MainMenuAction
import org.example.services.LocationService
import org.example.services.MineralService
import org.example.services.WorkerService
import org.example.ui.location.LocationMenu
import org.example.ui.mineral.MineralMenu
import org.example.ui.worker.WorkerMenu

/**
 * Application composition root: creates services/menus and runs the main loop.
 * Keeps single instances per run and wires dependencies explicitly for clarity/testability.
 */
class App {
    // Services
    private val mineralService = MineralService()
    private val locationService = LocationService()
    private val workerService = WorkerService(locationService)

    // Menus (UI)
    private val mineralMenu = MineralMenu(mineralService)
    private val locationMenu = LocationMenu(locationService, mineralService, workerService)
    private val workerMenu = WorkerMenu(workerService, locationService)
    private val mainMenu = MainMenu()

    /** Runs the app until the user chooses Exit. */
    fun run() {
        while (true) {
            when (mainMenu.run()) {
                MainMenuAction.Locations -> locationMenu.run()
                MainMenuAction.Minerals  -> mineralMenu.run()
                MainMenuAction.Workers   -> workerMenu.run()
                MainMenuAction.Exit      -> return
            }
        }
    }
}
