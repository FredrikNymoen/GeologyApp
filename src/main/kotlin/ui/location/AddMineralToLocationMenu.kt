package org.example.ui.location

import org.example.models.Mineral
import org.example.services.LocationService
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.ui.mineral.AddMineralMenu

class AddMineralToLocationMenu(
    private val locationService: LocationService,
    private val mineralService: MineralService
) {
    fun run() {
        println("\n=== Add Mineral to Location ===")

        val locationName = ConsoleIO.nonEmpty("Location name")

        val loc = locationService.get(locationName)
        if (loc == null) {
            println("Location '$locationName' not found.")
            return
        }

        // Reuse your AddMineralMenu, then link to location
        val addMineralFlow = AddMineralMenu(mineralService) { saved: Mineral ->
            try {
                locationService.addMineralToLocation(locationName, saved)
                println("Linked '${saved.name ?: "(unknown)"}' to '$locationName'.")
            } catch (ex: IllegalArgumentException) {
                println(ex.message)
            }
        }
        addMineralFlow.run()
    }
}
