package org.example.services

import org.example.models.Worker

class WorkerService {
    // key = employeeId
    private val workers = mutableListOf<Worker>();

    /** Returns true if a worker with this ID already exists. */
    fun exists(employeeId: String): Boolean =
        workers.any { it.workerId == employeeId }

    /** Add a new worker. Fails if the employee ID is already in use. */
    fun add(worker: Worker) {
        require(!exists(worker.workerId)) { "Employee ID already exists." }
        workers.add(worker)
    }

    /** Get a worker by ID, or null if not found. */
    fun get(employeeId: String): Worker? =
        workers.firstOrNull { it.workerId == employeeId }

    /** Replace the entire worker object with the same ID. */
    fun replace(employeeId: String, newWorker: Worker) {
        require(employeeId == newWorker.workerId) { "ID mismatch." }
        val idx = workers.indexOfFirst { it.workerId == employeeId }
        require(idx >= 0) { "Worker not found." }
        workers[idx] = newWorker
    }

    /** Apply a partial in-place update to the worker with the given ID. */
    fun update(employeeId: String, patch: Worker.() -> Unit) {
        val w = get(employeeId) ?: error("Worker not found.")
        w.patch()
    }

    /** Delete by ID. Returns true if at least one entry was removed. */
    fun delete(employeeId: String): Boolean =
        workers.removeAll { it.workerId == employeeId }

    /** Defensive copy so callers cannot mutate internal storage directly. */
    fun all(): List<Worker> = workers.toList()

    /** All workers who have at least one shift at the given location. */
    fun workersAt(locationId: String): List<Worker> =
        workers.filter { w -> w.shifts.any { it.location.locationId == locationId } }
}