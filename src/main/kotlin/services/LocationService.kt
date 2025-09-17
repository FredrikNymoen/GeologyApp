package org.example.services

import org.example.models.Location
import org.example.models.Mineral
import org.example.models.Worker
import org.example.utils.LocationLoader
import java.util.concurrent.atomic.AtomicInteger

/**
 * Service to manage locations.
 * Provides methods to list, add, get, and delete locations.
 * Also manages minerals and workers associated with each location.
 */
class LocationService {

    private val locations = mutableListOf<Location>()
    private val seq = AtomicInteger(0)           // thread-safe; starts at 0

    init {
        locations += LocationLoader.loadFromFile()
    }

    /** Generates the next unique ID as a string. */
    private fun nextId(): String = seq.incrementAndGet().toString();

    /** Returns all locations as a list. */
    fun listAll(): List<Location> {
        return locations
    }

    /** Get a location by its ID (case-insensitive). Returns null if not found. */
    fun get(id : String): Location? {
        return locations.find { it.locationId.equals(id, ignoreCase = true) }
    }

    /** Add a new location. Location names must be unique (case-insensitive). */
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

    /** Delete a location by ID (case-insensitive). Returns true if any removed. */
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

}