package org.example.ui.location

import org.example.services.LocationService
import org.example.ui.common.ConsoleIO

/**
 * Menu to add a new location.
 * Prompts for name, description, latitude, and longitude.
 * Validates that name is not empty, latitude is between -90 and 90,
 * and longitude is between -180 and 180.
 * Calls LocationService to create the location.
 */
class AddLocationMenu(
    private val locationService: LocationService
) {

    /** Runs the add location menu. */
    fun run() {
        println("\n=== Add Location ===")

        val name        = ConsoleIO.nonEmpty("Name")
        val description = ConsoleIO.optional("Description (optional)")
        val latitude    = ConsoleIO.doubleInRange("Latitude (-90..90)", -90.0, 90.0)
        val longitude   = ConsoleIO.doubleInRange("Longitude (-180..180)", -180.0, 180.0)

        try {
            locationService.add(name, description, latitude, longitude)
            println("Location '$name' added.")
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
    }
}
