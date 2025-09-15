package org.example.ui.location

import org.example.actions.location.LocationMenuAction
import org.example.services.LocationService
import org.example.services.MineralService
import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

class LocationMenu (
    private val locationService: LocationService,
    mineralService: MineralService,
    workerService: WorkerService
) {

    private val addLocationMenu = AddLocationMenu(locationService)
    private val updateLocationMenu = UpdateLocationMenu(locationService)
    private val addMineralToLocationMenu = AddMineralToLocationMenu(locationService, mineralService)
    private val addWorkerToLocationMenu = AddWorkerToLocationMenu(locationService, workerService)
    private val options = LocationMenuAction.entries.map { "${it.shortcut} - ${it.label}" }



    fun run() {

        while (true) {
            // Show menu and read choice
            ConsoleIO.showMenu("Location Menu", options)
            val choice = ConsoleIO.choice()

            val action = fromInput<LocationMenuAction>(choice)
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            when (action) {

                // 1) List all
                LocationMenuAction.ListAll -> {
                    val all = locationService.listAll()
                    if (all.isEmpty()) println("No locations found.")
                    else all.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Location.toString()
                }

                // 2) List minerals at location
                LocationMenuAction.ListMinerals -> {
                    print("Enter location name: ")
                    val locName = ConsoleIO.choice()
                    val minerals = locationService.listMineralsAtLocation(locName)
                    if (minerals == null) println("No minerals found at this location or location does not exist.")
                    else minerals.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Mineral.toString()
                }

                // 3) Add location
                LocationMenuAction.Add -> {
                    addLocationMenu.run()
                }

                // 4) Update location
                LocationMenuAction.Update -> {
                    updateLocationMenu.run()
                }

                // 5) Delete location
                LocationMenuAction.Delete -> {
                    print("Enter location name to delete: ")
                    val locName = ConsoleIO.choice()
                    val success = locationService.delete(locName)
                    if (success) println("Location '${locName}' deleted.")
                    else println("Location '${locName}' not found.")

                }

                // 6) Add mineral to location
                LocationMenuAction.AddMineral -> {
                    addMineralToLocationMenu.run()
                }

                // 7) Add worker to location
                LocationMenuAction.AddWorker -> {
                    addWorkerToLocationMenu.run()
                }

                // B) Go back
                LocationMenuAction.Back -> return
            }
        }
    }
}