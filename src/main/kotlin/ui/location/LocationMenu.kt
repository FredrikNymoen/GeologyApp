package org.example.ui.location

import org.example.services.LocationService

class LocationMenu (
    val locationService: LocationService
){
    val locationMenuList = listOf(
        "1 - List all locations",
        "2 - Add location",
        "3 - Update location",
        "4 - Delete location",
        "5 - Return to main menu"
    )

    fun run() {
        while (true) {
            Input.showMenu("Location Menu", locationMenuList)
            when (Input.choice()) {
                "1" -> {/* call service.add(...) */}
                "2" -> {/* call service.update(...) */}
                "3" -> {/* call service.delete(...) */}
                "4" -> {}
                "5" -> return
                else -> println("Invalid choice")
            }
        }
    }
}