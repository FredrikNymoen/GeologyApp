package org.example.ui.location

import org.example.actions.location.LocationMenuAction
import org.example.services.LocationService
import org.example.services.MineralService
import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

/**
 * Menu to manage locations.
 * Uses LocationService for CRUD operations.
 * Uses WorkerService to list workers at a location.
 */
class LocationMenu (
    private val locationService: LocationService,
    mineralService: MineralService,
    private val workerService: WorkerService
) {

    private val addLocationMenu = AddLocationMenu(locationService)
    private val updateLocationMenu = UpdateLocationMenu(locationService, mineralService)
    private val options = LocationMenuAction.entries.map { "${it.shortcut} - ${it.label}" }



    /** Main loop; returns when user chooses Back. */
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

                // 2) List workers at location
                LocationMenuAction.ListWorkers -> {
                    print("Enter location id: ")
                    val locId = ConsoleIO.choice()
                    val workers = workerService.workersAt(locId)
                    if (workers.isEmpty()) println("No workers found at this location or location does not exist.")
                    else workers.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Worker.toString()
                }

                // 3) List minerals at location
                LocationMenuAction.ListMinerals -> {
                    print("Enter location id: ")
                    val locId = ConsoleIO.choice()
                    val minerals = locationService.listMineralsAtLocation(locId)
                    if (minerals?.isEmpty() ?: true) {
                        println("No minerals found at this location or location does not exist.")
                    } else {
                        minerals.forEachIndexed { i, m -> println("${i + 1}. $m") }
                    }}

                // 4) Add location
                LocationMenuAction.Add -> {
                    addLocationMenu.run()
                }

                // 5) Update location
                LocationMenuAction.Update -> {
                    updateLocationMenu.run()
                }

                // 6) Delete location
                LocationMenuAction.Delete -> {
                    print("Enter location id to delete: ")
                    val locId = ConsoleIO.choice()
                    val success = locationService.delete(locId)
                    if (success) println("Location with id: '${locId}' deleted.")
                    else println("Location with id: '${locId}' not found.")
                }

                // B) Go back
                LocationMenuAction.Back -> return
            }
        }
    }
}