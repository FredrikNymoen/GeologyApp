package org.example.ui.location

import org.example.models.Location
import org.example.services.LocationService
import org.example.ui.common.ConsoleIO

class AddLocationMenu(
    private val locationService: LocationService
) {
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
