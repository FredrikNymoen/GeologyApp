package org.example.services

import org.example.models.Location
import org.example.models.Mineral
import org.example.models.Worker
import org.example.utils.LocationLoader
import java.util.concurrent.atomic.AtomicInteger

class LocationService {

    private val locations = mutableListOf<Location>()
    private val seq = AtomicInteger(0)           // thread-safe; starts at 0

    init {
        locations += LocationLoader.loadFromFile()
    }

    private fun nextId(): String = seq.incrementAndGet().toString();

    // --- CRUD on locations list ---
    fun listAll(): List<Location> {
        return locations
    }


    fun get(id : String): Location? {
        return locations.find { it.locationId.equals(id, ignoreCase = true) }
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

    fun delete(id: String): Boolean {
        return locations.removeIf { it.locationId.equals(id, ignoreCase = true) }
    }



    // -------- Minerals --------

    /** List minerals at a location */
    fun listMineralsAtLocation(id: String): List<Mineral> {
        val location = locations.firstOrNull { it.locationId.equals(id, ignoreCase = true) }
        return location?.getMinerals() ?: emptyList()
    }

    /** Add a mineral to a location (prevents duplicates by mineral name, case-insensitive). */
    fun addMineralToLocation(locationId: String, mineral: Mineral) {
        val loc = get(locationId) ?: error("Location '${get(locationId)?.name}' not found.")
        val mName = mineral.name ?: error("Mineral name cannot be null.")
        val exists = loc.getMinerals().any { it.name?.equals(mName, true) == true }
        require(!exists) { "Mineral '$mName' already exists at '${get(locationId)?.name}'." }
        loc.addMineral(mineral)
    }

    /** Remove a mineral by name at a location. Returns true if any removed. */
    fun removeMineral(locationId: String, mineralName: String): Boolean {
        val loc = get(locationId) ?: error("Location '${get(locationId)?.name}' not found.")
        val target = loc.getMinerals().firstOrNull { it.name?.equals(mineralName, true) == true }
            ?: return false
        loc.removeMineral(target)
        return true
    }

    /** Update the first mineral matching name using a patch lambda. */
    fun updateMineral(locationId: String, mineralName: String, patch: Mineral.() -> Unit): Boolean {
        val loc = get(locationId) ?: error("Location '${get(locationId)?.name}' not found.")
        val target = loc.getMinerals().firstOrNull { it.name?.equals(mineralName, true) == true }
            ?: return false
        target.patch()
        return true
    }

    // -------- Workers --------

    /** List workers at a location (empty if none or location missing). */
    fun listWorkersAtLocation(locationId: String): List<Worker> {
        val loc = get(locationId) ?: return emptyList()
        return loc.getWorkers()
    }

    /** Add a worker to a location (prevents duplicates by employeeId). */
    fun addWorkerToLocation(locationId: String, worker: Worker) : Boolean {
        val loc = get(locationId) ?: error("Location '${get(locationId)?.name}' not found.")
        val exists = loc.getWorkers().any { it.workerId == worker.workerId }
        if (exists) return false
        loc.addWorker(worker)
        return true
    }

    /** Remove a worker by employeeId at a location. Returns true if any removed. */
    fun removeWorkerById(locationId : String, workerId: String): Boolean {
        val loc = get(locationId) ?: error("Location '${get(locationId)?.name}' not found.")
        val target = loc.getWorkers().firstOrNull { it.workerId == workerId }
            ?: return false
        loc.removeWorker(target)
        return true
    }

}