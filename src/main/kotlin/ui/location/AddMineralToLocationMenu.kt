package org.example.ui.location

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

        // Build an interactive flow for adding/selecting a mineral, then link it to a location.
        val addMineralFlow = AddMineralMenu(
            service = mineralService,          // Mineral catalog/service (stores and lists minerals)
            afterSave = { saved ->             // Callback that runs AFTER a mineral is saved/selected
                try {
                    // Link the chosen/saved mineral to the given location in LocationService
                    locationService.addMineralToLocation(locationName, saved)

                    // Print confirmation. If name is null, show "(unknown)" instead.
                    println("Linked '${saved.name ?: "(unknown)"}' to '$locationName'.")
                } catch (ex: IllegalArgumentException) {
                    // Link can fail (e.g., location not found or duplicate mineral at that location)
                    // LocationService throws IllegalArgumentException; we surface the message.
                    println(ex.message)
                }
            },
            allowSelectExisting = true         // Offer the user to pick an EXISTING mineral before creating a new one
        )

        // Start the UI flow (prompts the user, validates inputs, saves/chooses a mineral,
        // then invokes the callback above to link it to the location).
        addMineralFlow.run()

    }
}
