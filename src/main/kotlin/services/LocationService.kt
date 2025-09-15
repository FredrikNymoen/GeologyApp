package org.example.services

import org.example.models.Location
import org.example.models.Mineral
import org.example.models.Worker
import java.util.concurrent.atomic.AtomicInteger

class LocationService {

    private val locations = mutableListOf<Location>()
    private val seq = AtomicInteger(0)           // thread-safe; starts at 0

    private fun nextId(): String = seq.incrementAndGet().toString();

    // --- CRUD on locations list ---
    fun listAll(): List<Location> {
        return locations
    }

    fun get(name: String): Location? {
        return locations.find { it.name.equals(name, ignoreCase = true) }
    }


    fun add(
        name: String,
        description: String?,
        latitude: Double,
        longitude: Double
    ): Location {
        require(get(name) == null) { "Location with name '$name' already exists." }

        val loc = Location(
            locationId = nextId(),
            name = name,
            description = description,
            latitude = latitude,
            longitude = longitude
        )
        locations.add(loc)
        return loc
    }

    fun delete(locationName: String): Boolean {
        return locations.removeIf { it.name == locationName }
    }


    /** Patch-style update; blank/NULL means keep current. */
    fun updateLocation(
        currentName: String,
        newName: String? = null,
        newDescription: String? = null,
        newLatitude: Double? = null,
        newLongitude: Double? = null
    ): Location {
        val loc = get(currentName) ?: error("Location '$currentName' not found.")
        newName?.let {
            require(it.equals(currentName, ignoreCase = true) || get(it) == null) {
                "Location with name '$it' already exists."
            }
            loc.name = it
        }
        newDescription?.let { loc.description = it }
        if (newLatitude != null || newLongitude != null) {
            val lat = newLatitude ?: loc.latitude
            val lon = newLongitude ?: loc.longitude
            loc.latitude = lat
            loc.longitude = lon
        }
        return loc
    }

    // -------- Minerals --------

    /** List minerals at a location */
    fun listMineralsAtLocation(location: String): List<Mineral>? {
        // Placeholder implementation
        val location = locations.find { it.name == location }
        return location?.getMinerals()
    }

    /** Add a mineral to a location (prevents duplicates by mineral name, case-insensitive). */
    fun addMineralToLocation(locationName: String, mineral: Mineral) {
        val loc = get(locationName) ?: error("Location '$locationName' not found.")
        val mName = mineral.name ?: error("Mineral name cannot be null.")
        val exists = loc.getMinerals()?.any { it.name?.equals(mName, true) == true } == true
        require(!exists) { "Mineral '$mName' already exists at '$locationName'." }
        loc.addMineral(mineral)
    }

    /** Remove a mineral by name at a location. Returns true if any removed. */
    fun removeMineralByName(locationName: String, mineralName: String): Boolean {
        val loc = get(locationName) ?: error("Location '$locationName' not found.")
        val target = loc.getMinerals()?.firstOrNull { it.name?.equals(mineralName, true) == true } ?: return false
        loc.removeMineral(target)
        return true
    }

    /** Update the first mineral matching name using a patch lambda. */
    fun updateMineralByName(locationName: String, mineralName: String, patch: Mineral.() -> Unit): Boolean {
        val loc = get(locationName) ?: error("Location '$locationName' not found.")
        // getMinerals() returns a copy of the list, but ELEMENTS are the same references.
        val m = loc.getMinerals()?.firstOrNull { it.name?.equals(mineralName, true) == true } ?: return false
        m.patch() // mutating 'm' mutates the same Mineral instance stored in Location
        return true
    }

    // -------- Workers --------

    /** List workers at a location (empty if none or location missing). */
    fun listWorkersAtLocation(locationName: String): List<Worker> =
        get(locationName)?.getWorkers().orEmpty()

    /** Add a worker to a location (prevents duplicates by employeeId). */
    fun addWorkerToLocation(locationName: String, worker: Worker) {
        val loc = get(locationName) ?: error("Location '$locationName' not found.")
        val exists = loc.getWorkers()?.any { it.workerId == worker.workerId } == true
        require(!exists) { "Worker '${worker.workerId}' already exists at '$locationName'." }
        loc.addWorker(worker)
    }

    /** Remove a worker by employeeId at a location. Returns true if any removed. */
    fun removeWorkerById(locationName: String, employeeId: String): Boolean {
        val loc = get(locationName) ?: error("Location '$locationName' not found.")
        val target = loc.getWorkers()?.firstOrNull { it.workerId == employeeId } ?: return false
        loc.removeWorker(target)
        return true
    }

}