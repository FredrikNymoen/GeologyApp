// org/example/ui/location/UpdateLocationMenu.kt
package org.example.ui.location

import org.example.actions.location.UpdateLocationMenuAction
import org.example.models.Location
import org.example.services.LocationService
import org.example.services.MineralService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

class UpdateLocationMenu(
    private val locationService: LocationService,
    mineralService: MineralService
) {
    private val addMineralToLocationMenu = AddMineralToLocationMenu(locationService, mineralService)
    private val options = UpdateLocationMenuAction.entries.map { "${it.shortcut} - ${it.label}" }

    fun run() {
        println("\n=== Update Location ===")
        val id = ConsoleIO.nonEmpty("Enter location id")
        val loc = locationService.get(id)
        if (loc == null) {
            println("Location with id: '$id' not found.")
            return
        }

        while (true) {
            // Vis alltid fersk info i tittelen
            val headerName = loc.name ?: "(unnamed)"
            ConsoleIO.showMenu("Update $headerName [${loc.locationId}]", options)

            val action = fromInput<UpdateLocationMenuAction>(ConsoleIO.choice())
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            when (action) {
                UpdateLocationMenuAction.EditInfo     -> editInfo(loc)
                UpdateLocationMenuAction.AddMineral -> addMineralToLocationMenu.run (loc)
                UpdateLocationMenuAction.RemoveMineral-> removeMineral(loc)
                UpdateLocationMenuAction.Back         -> return
            }
        }
    }

    // --- Actions ---

    /** Edit name/description/lat/lon (blank = keep). Enforces unique name (case-insensitive). */
    private fun editInfo(loc: Location) {
        println("\n--- Edit Location Information ---")
        println("Leave blank to keep current value.")

        val newName = run {
            val raw = ConsoleIO.prompt("Name [${loc.name ?: "keep"}]").trim()
            raw.ifBlank { loc.name }
        }

        // Unik navn-sjekk (case-insensitive) mot andre lokasjoner
        newName?.let { nn ->
            val clash = locationService.listAll().any { it !== loc && it.name?.equals(nn, true) == true }
            if (clash) {
                println("A location named '$nn' already exists. Keeping previous name.")
            } else {
                loc.name = nn
            }
        }

        val newDesc = ConsoleIO.prompt("Description [${loc.description ?: "keep"}]").trim()
        if (newDesc.isNotBlank()) loc.description = newDesc

        // Latitude
        val latIn = ConsoleIO.prompt("Latitude (-90..90) [${loc.latitude}]").trim()
        if (latIn.isNotBlank()) {
            val v = latIn.replace(',', '.').toDoubleOrNull()
            if (v == null || v !in -90.0..90.0) {
                println("Invalid latitude. Keeping previous value.")
            } else loc.latitude = v
        }

        // Longitude
        val lonIn = ConsoleIO.prompt("Longitude (-180..180) [${loc.longitude}]").trim()
        if (lonIn.isNotBlank()) {
            val v = lonIn.replace(',', '.').toDoubleOrNull()
            if (v == null || v !in -180.0..180.0) {
                println("Invalid longitude. Keeping previous value.")
            } else loc.longitude = v
        }

        println("✔ Location updated.")
    }


    /** Remove a mineral by name (case-insensitive). */
    private fun removeMineral(loc: Location) {
        val name = ConsoleIO.nonEmpty("Mineral name to remove").trim()
        val ok = locationService.removeMineral(loc.locationId, name)
        if (ok) println("✔ Removed '$name' from ${loc.name ?: loc.locationId}.")
        else    println("Mineral '$name' not found at ${loc.name ?: loc.locationId}.")
    }
}
