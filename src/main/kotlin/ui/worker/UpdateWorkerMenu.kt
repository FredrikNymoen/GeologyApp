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

class UpdateWorkerMenu(
    private val workerService: WorkerService,
    private val locationService: LocationService
) {
    private val options = UpdateWorkerMenuAction.entries.map { "${it.shortcut} - ${it.label}" }
    private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

    /** Runs the update flow for a single worker. */
    fun run() {
        println("\n=== Update Worker ===")
        val workerId = ConsoleIO.nonEmpty("Worker ID")
        if (workerService.get(workerId) == null) {
            println("Worker not found.")
            return
        }

        while (true) {
            val current = workerService.get(workerId)!! // always show latest name
            ConsoleIO.showMenu("Update ${current.getFullName()} [${current.workerId}]", options)

            val action = fromInput<UpdateWorkerMenuAction>(ConsoleIO.choice())
            if (action == null) {
                println("Invalid choice. Please try again.")
                continue
            }

            when (action) {
                UpdateWorkerMenuAction.EditWorkerInformation -> editWorkerInfo(workerId)
                UpdateWorkerMenuAction.AddOrReplaceShift    -> addOrReplaceShift(workerId)
                UpdateWorkerMenuAction.RemoveShift          -> removeShift(workerId)
                UpdateWorkerMenuAction.ListShifts           -> printShifts(workerService.get(workerId)!!)
                UpdateWorkerMenuAction.Back                 -> return
            }
        }
    }

    // -------- Actions --------

    /** Edit first name, last name, phone in one go (blank = keep current). */
    private fun editWorkerInfo(workerId: String) {
        val w = workerService.get(workerId)!!

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
            val idx = getShifts().indexOfFirst { it.day == day } // find existing shift that day
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
    private fun promptOptional(label: String): String? {
        val v = ConsoleIO.prompt(label)
        return v.ifBlank { null }
    }

    /** Accepts 1..7 (Mon..Sun) or names (Mon/MONDAY). Blank cancels. */
    private fun promptDayOfWeek(): DayOfWeek? {
        val raw = ConsoleIO.prompt("Day (1=Mon … 7=Sun, or name; blank=cancel)")
        if (raw.isBlank()) return null

        raw.toIntOrNull()?.let { n ->
            if (n in 1..7) return DayOfWeek.of(n)
        }
        val up = raw.trim().uppercase()
        return DayOfWeek.entries.firstOrNull {
            it.name == up || it.name.startsWith(up) || it.name.substring(0, 3) == up.take(3)
        }.also {
            if (it == null) println("Unrecognized day: '$raw'")
        }
    }

    /** Parses times like 8:00 or 15:30. Blank cancels. */
    private fun promptTime(label: String): LocalTime? {
        while (true) {
            val raw = ConsoleIO.prompt("$label (blank=cancel)")
            if (raw.isBlank()) return null
            try {
                return LocalTime.parse(raw.trim(), timeFmt)
            } catch (_: Exception) {
                println("Invalid time. Use H:mm, e.g., 8:00 or 15:30.")
            }
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

    /** Lookup by ID first, then by name (case-insensitive). Blank cancels. */
    private fun promptLocation(): Location? {
        val key = ConsoleIO.prompt("Location ID or name (blank=cancel)")
        if (key.isBlank()) return null

        // Try by exact ID
        locationService.listAll().firstOrNull { it.locationId == key }?.let { return it }

        // Try by name (case-insensitive)
        val byName = locationService.get(key)
        if (byName != null) return byName

        println("Location not found: '$key'.")
        return null
    }
}
