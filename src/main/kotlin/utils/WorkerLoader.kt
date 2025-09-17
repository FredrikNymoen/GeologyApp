package org.example.utils

import org.example.models.WorkShift
import org.example.models.Worker
import org.example.services.LocationService
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Loads workers and shifts from `/workers.txt`: accepts `WORKER <id> <first> <last> <phone>` and
 * `SHIFT <workerId> <DAY> <START> <END> <LocationId|Name> <HourlyWage>`,
 * ignores blank/# lines, parses day by number or name,
 * times as `H:mm`, wage with `.` or `,`, keeps at most one shift per weekday (replacing if present),
 * and links each worker to the shift's location.
 */
object WorkerLoader {

    /** Split on tab(s) or runs of 2+ spaces. */
    private val splitter = Regex("\\t+|\\s{2,}")
    private val timeFmt  = DateTimeFormatter.ofPattern("H:mm")

    fun loadFromFile(locationService: LocationService, resourcePath: String = "/workers.txt"): List<Worker> {
        val input = javaClass.getResourceAsStream(resourcePath) ?: return emptyList()
        val byId = linkedMapOf<String, Worker>() // preserve file order

        input.bufferedReader().useLines { lines ->
            lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val p = line.split(splitter).map { it.trim() }
                    if (p.isEmpty()) return@forEach

                    when (p[0].uppercase()) {
                        "WORKER" -> {
                            if (p.size < 5) return@forEach
                            val id = p[1]
                            if (id.isBlank()) return@forEach
                            if (!byId.containsKey(id)) {
                                byId[id] = Worker(
                                    workerId = id,
                                    firstName = p[2],
                                    lastName = p[3],
                                    phone = p[4],
                                    shifts = mutableListOf()
                                )
                            }
                        }
                        "SHIFT" -> {
                            if (p.size < 7) return@forEach
                            val worker = byId[p[1]] ?: return@forEach
                            val day = parseDay(p[2]) ?: return@forEach
                            val start = runCatching { LocalTime.parse(p[3], timeFmt) }.getOrNull() ?: return@forEach
                            val end   = runCatching { LocalTime.parse(p[4], timeFmt) }.getOrNull() ?: return@forEach
                            val wage  = p[6].replace(',', '.').toDoubleOrNull() ?: return@forEach
                            val loc   = resolveLocation(p[5], locationService) ?: return@forEach

                            val shift = WorkShift(day, start, end, loc, wage)

                            // Replace existing shift for same weekday; otherwise add.
                            val idx = worker.getShifts().indexOfFirst { it.day == day }
                            if (idx >= 0) worker.replaceShift(idx, shift) else worker.addShift(shift)

                            // Ensure location lists this worker once.
                            if (loc.getWorkers().none { it.workerId == worker.workerId }) loc.addWorker(worker)
                        }
                    }
                }
        }
        return byId.values.toList()
    }

    private fun parseDay(raw: String): DayOfWeek? {
        raw.toIntOrNull()?.let { n -> if (n in 1..7) return DayOfWeek.of(n) }
        val up = raw.trim().uppercase()
        return DayOfWeek.entries.firstOrNull {
            it.name == up || it.name.startsWith(up) || it.name.take(3) == up.take(3)
        }
    }

    private fun resolveLocation(key: String, svc: LocationService) =
        svc.get(key) ?: svc.listAll().firstOrNull { it.name?.equals(key, true) == true }
}
