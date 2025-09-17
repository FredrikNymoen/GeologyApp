package org.example.ui.worker

import org.example.actions.worker.UpdateWorkerMenuAction
import org.example.models.Location
import org.example.models.WorkShift
import org.example.models.Worker
import org.example.services.LocationService
import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Console submenu to update a single [Worker]: edit basic info, add/replace/remove
 * one weekly shift per weekday, and list current shifts. Input is validated and
 * re-prompted where sensible; actions are idempotent and safe to repeat.
 */
class UpdateWorkerMenu(
    private val workerService: WorkerService,
    private val locationService: LocationService
) {
    private val options = UpdateWorkerMenuAction.entries.map { "${it.shortcut} - ${it.label}" }
    private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

    /** Runs the update loop for the chosen worker (returns on Back). */
    fun run() {
        println("\n=== Update Worker ===")
        val workerId = ConsoleIO.nonEmpty("Worker ID").trim()
        val initial = workerService.get(workerId)
        if (initial == null) {
            println("Worker not found.")
            return
        }

        while (true) {
            // Fetch fresh each turn in case we mutated the worker
            val current = workerService.get(workerId) ?: run {
                println("Worker deleted while editing; returning.")
                return
            }
            ConsoleIO.showMenu("Update ${current.getFullName()} [${current.workerId}]", options)

            val action = fromInput<UpdateWorkerMenuAction>(ConsoleIO.choice().trim())
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            when (action) {
                UpdateWorkerMenuAction.EditWorkerInformation -> editWorkerInfo(workerId)
                UpdateWorkerMenuAction.AddOrReplaceShift    -> addOrReplaceShift(workerId)
                UpdateWorkerMenuAction.RemoveShift          -> removeShift(workerId)
                UpdateWorkerMenuAction.ListShifts           -> printShifts(current)
                UpdateWorkerMenuAction.Back                 -> return
            }
        }
    }

    // -------- Actions --------

    /** Edit first/last/phone (blank keeps current). */
    private fun editWorkerInfo(workerId: String) {
        val w = workerService.get(workerId) ?: return
        println("\n--- Edit Worker Information ---")
        println("Leave blank to keep current value.")

        val newFirst = promptOptional("First name [${w.firstName}]")
        val newLast  = promptOptional("Last name  [${w.lastName}]")
        val newPhone = promptOptional("Phone      [${w.phone}]")

        workerService.update(workerId) {
            newFirst?.let { firstName = it }
            newLast?.let  { lastName  = it }
            newPhone?.let { phone     = it }
        }
        println("✔ Worker information updated.")
    }

    /** Add or replace the single shift for a weekday. */
    private fun addOrReplaceShift(workerId: String) {
        println("\n--- Add/Replace Shift ---")
        val day = promptDayOfWeek() ?: return
        val location = promptLocation() ?: return
        val start = promptTime("Start time (H:mm)") ?: return
        val end = promptTime("End time (H:mm)") ?: return
        if (!end.isAfter(start)) {
            println("End must be after start.")
            return
        }
        val wage = promptNonNegativeDouble("Hourly wage")

        workerService.update(workerId) {
            val idx = getShifts().indexOfFirst { it.day == day } // max 1 per weekday
            val newShift = WorkShift(day = day, start = start, end = end, location = location, hourlyWage = wage)
            if (idx >= 0) {
                replaceShift(idx, newShift)
                println("✔ Replaced $day shift.")
            } else {
                addShift(newShift)
                println("✔ Added $day shift.")
            }
        }
    }

    /** Remove the shift for a given weekday. */
    private fun removeShift(workerId: String) {
        println("\n--- Remove Shift ---")
        val day = promptDayOfWeek() ?: return
        val removed = workerService.removeShift(workerId, day)
        if (removed) println("✔ Removed $day shift.") else println("No shift found on $day.")
    }

    // -------- Rendering --------

    private fun printShifts(worker: Worker) {
        val shifts = worker.getShifts()
        if (shifts.isEmpty()) {
            println("No shifts.")
            return
        }
        println("\nShifts:")
        shifts.sortedBy { it.day.value }.forEachIndexed { i, s ->
            val loc = s.location
            val locLabel = "${loc.name ?: "(unnamed)"} [${loc.locationId}]"
            println("${i + 1}. ${s.day}: ${s.start}–${s.end} @ $locLabel — ${"%.2f".format(s.hourlyWage)}/h")
        }
    }

    // -------- Prompts --------

    /** Returns null to cancel. */
    private fun promptOptional(label: String): String? =
        ConsoleIO.prompt(label).ifBlank { null }

    /** Accepts 1..7 (Mon..Sun) or names/prefixes (Mon/MONDAY). Blank cancels. */
    private fun promptDayOfWeek(): DayOfWeek? {
        val raw = ConsoleIO.prompt("Day (1=Mon … 7=Sun, or name; blank=cancel)")
        if (raw.isBlank()) return null

        raw.toIntOrNull()?.let { n -> if (n in 1..7) return DayOfWeek.of(n) }
        val up = raw.trim().uppercase()
        return DayOfWeek.entries.firstOrNull {
            it.name == up || it.name.startsWith(up) || it.name.take(3) == up.take(3)
        }.also {
            if (it == null) println("Unrecognized day: '$raw'")
        }
    }

    /** Parses times like 8:00 or 15:30. Blank cancels. */
    private fun promptTime(label: String): LocalTime? {
        while (true) {
            val raw = ConsoleIO.prompt("$label (blank=cancel)")
            if (raw.isBlank()) return null
            runCatching { LocalTime.parse(raw.trim(), timeFmt) }
                .onSuccess { return it }
                .onFailure { println("Invalid time. Use H:mm, e.g., 8:00 or 15:30.") }
        }
    }

    /** Non-negative double; accepts comma or dot. */
    private fun promptNonNegativeDouble(label: String): Double {
        while (true) {
            val raw = ConsoleIO.prompt(label)
            val v = raw.replace(',', '.').toDoubleOrNull()
            if (v != null && v >= 0.0) return v
            println("Please enter a non-negative number (e.g., 0, 200, 199.5).")
        }
    }

    /** Lookup by exact ID (case-insensitive) or by name (case-insensitive). Blank cancels. */
    private fun promptLocation(): Location? {
        val key = ConsoleIO.prompt("Location ID or name (blank=cancel)").trim()
        if (key.isBlank()) return null

        // Try ID first (case-insensitive), then by name (case-insensitive)
        return locationService.listAll().firstOrNull {
            it.locationId.equals(key, ignoreCase = true)
        } ?: locationService.listAll().firstOrNull {
            it.name?.equals(key, ignoreCase = true) == true
        } ?: run {
            println("Location not found: '$key'.")
            null
        }
    }
}
