package org.example.ui.location

import org.example.services.LocationService
import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO

class AddWorkerToLocationMenu(
    private val locationService: LocationService,
    private val workerService: WorkerService   // ← inject, don't new()
) {
    fun run() {
        println("\n=== Add Worker to Location ===")

        val locationName = ConsoleIO.nonEmpty("Location name")
        val employeeId   = ConsoleIO.nonEmpty("Employee ID")

        val worker = workerService.get(employeeId)
        if (worker == null) {
            println("Worker '$employeeId' not found. Add the worker first, then link to location.")
            return
        }

        try {
            locationService.addWorkerToLocation(locationName, worker)
            println("Worker '${worker.workerId}' added to '$locationName'.")
        } catch (ex: IllegalArgumentException) {
            println(ex.message)
        }
    }
}
