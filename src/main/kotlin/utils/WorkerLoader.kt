package org.example.utils

import org.example.models.WorkShift
import org.example.models.Worker
import org.example.services.LocationService
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object WorkerLoader {

    private val splitter = Regex("\\t+|\\s{2,}") // tab eller 2+ space
    private val timeFmt  = DateTimeFormatter.ofPattern("H:mm")

    fun loadFromFile(locationService: LocationService): List<Worker> {
        val input = javaClass.getResourceAsStream("/workers.txt") ?: return emptyList()
        val byId = linkedMapOf<String, Worker>() // bevar rekkefølge

        input.bufferedReader().useLines { lines ->
            lines.map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val p = line.split(splitter).map { it.trim() }
                    if (p.isEmpty()) return@forEach

                    when (p[0].uppercase()) {
                        "WORKER" -> {
                            // WORKER <id> <first> <last> <phone>
                            if (p.size >= 5) {
                                val id = p[1]
                                val first = p[2]
                                val last = p[3]
                                val phone = p[4]
                                if (id.isNotBlank()) {
                                    byId[id] = Worker(
                                        workerId = id,
                                        firstName = first,
                                        lastName = last,
                                        phone = phone,
                                        shifts = mutableListOf()
                                    )
                                }
                            }
                        }
                        "SHIFT" -> {
                            // SHIFT <workerId> <DAY> <START> <END> <LocationId> <HourlyWage>
                            if (p.size >= 7) {
                                val workerId = p[1]
                                val worker = byId[workerId] ?: return@forEach

                                val day = parseDay(p[2]) ?: return@forEach
                                val start = LocalTime.parse(p[3], timeFmt)
                                val end   = LocalTime.parse(p[4], timeFmt)
                                val locId = p[5]
                                val wage  = p[6].replace(',', '.').toDoubleOrNull() ?: return@forEach

                                val loc = locationService.get(locId)
                                    ?: locationService.listAll().firstOrNull { it.locationId == locId }
                                    ?: return@forEach

                                val shift = WorkShift(day = day, start = start, end = end, location = loc, hourlyWage = wage)

                                // maks 1 skift pr ukedag -> erstatt hvis finnes
                                val idx = worker.getShifts().indexOfFirst { it.day == day }
                                if (idx >= 0) worker.replaceShift(idx, shift) else worker.addShift(shift)

                                // link worker til location, så "Workers (N)" vises i Location-lista
                                val alreadyLinked = loc.getWorkers().any { it.workerId == worker.workerId }
                                if (!alreadyLinked) loc.addWorker(worker)
                            }
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
}
