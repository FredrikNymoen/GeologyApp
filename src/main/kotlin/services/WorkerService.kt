package org.example.services

import org.example.models.WorkShift
import org.example.models.Worker
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.round

class WorkerService {
    // key = employeeId
    private val workers = mutableListOf<Worker>();
    private val seq = AtomicInteger(0)

    /** ~ average weeks per month: 52 weeks / 12 months */
    private val WEEKS_PER_MONTH = 52.0 / 12.0


    private fun nextId(): String = seq.incrementAndGet().toString()


    /** Create + add a worker with an auto-generated ID. */
    fun create(firstName: String, lastName: String, phone: String): Worker {
        val w = Worker(
            workerId = nextId(),
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            shifts = mutableListOf()
        )
        workers.add(w)
        return w
    }

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
    fun getAll(): List<Worker> = workers.toList()

    /** All workers who have at least one shift at the given location. */
    fun workersAt(locationId: String): List<Worker> =
        workers.filter { w -> w.getShifts().any { it.location.locationId == locationId } }


    /**  -------  Methods for calculating paychecks --------*/

    // ---- enforce: max one shift per weekday ----

    /**
     * Set (upsert) the shift for a given weekday.
     * If a shift already exists on that weekday, it will be replaced when replaceIfExists=true (default).
     * If replaceIfExists=false and a shift exists, throws.
     */
    fun setShift(workerId: String, shift: WorkShift, replaceIfExists: Boolean = true) {
        val w = get(workerId) ?: error("Worker not found.")
        val idx = w.getShifts().indexOfFirst { it.day == shift.day }
        if (idx >= 0) {
            require(replaceIfExists) { "A shift for ${shift.day} already exists." }
            w.replaceShift(idx, shift)
        } else {
            w.addShift(shift)
        }
    }

    /** Remove the shift for a given weekday. Returns true if removed. */
    fun removeShift(workerId: String, day: DayOfWeek): Boolean {
        val w = get(workerId) ?: error("Worker not found.")
        val target = w.getShifts().firstOrNull { it.day == day } ?: return false
        return w.removeShift(target)
    }

    // ---- weekly & monthly (avg) calculations ----

    /** Total weekly hours from the current one-shift-per-day schedule. */
    fun weeklyHours(worker: Worker): Double {
        var total = 0.0
        for (shift in worker.getShifts()) {
            val minutes = ChronoUnit.MINUTES.between(shift.start, shift.end)
            require(minutes > 0) { "Shift end must be after start." }
            total += minutes / 60.0
        }
        return round2(total)
    }

    /** Weekly pay: sums (hours × shift.hourlyWage) across the week. */
    fun weeklyPay(worker: Worker): Double {
        var total = 0.0
        for (shift in worker.getShifts()) {
            val hours = ChronoUnit.MINUTES.between(shift.start, shift.end) / 60.0
            total += hours * shift.hourlyWage
        }
        return round2(total)
    }

    /** Typical monthly pay = weeklyPay × (52/12). */
    fun monthlyPayTypical(worker: Worker): Double =
        round2(weeklyPay(worker) * WEEKS_PER_MONTH)

    /** Typical monthly paycheck for one worker ID (null if not found). */
    fun calculatePaycheckForWorkerTypical(workerId: String): Double? =
        get(workerId)?.let { monthlyPayTypical(it) }

    /** Typical monthly paychecks for all workers. */
    fun calculatePaychecksForAllTypical(): Map<Worker, Double> =
        workers.associateWith { w -> monthlyPayTypical(w) }

    private fun round2(x: Double): Double = round(x * 100.0) / 100.0
}