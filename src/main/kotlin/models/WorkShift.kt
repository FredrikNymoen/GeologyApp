package org.example.models

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

/** A single work shift for a [Worker], on a specific day of the week, with start/end times,
 *  location, and hourly wage. */
class WorkShift (
    val day: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
    val location: Location,
    val hourlyWage: Double
) {
    init {
        require(end.isAfter(start)) { "Shift end must be after start (same day)." }
        require(hourlyWage >= 0.0)  { "Hourly wage must be non-negative." }
    }

    /** Length on the shift in hours */
    fun hours(): Double {
        val minutes = ChronoUnit.MINUTES.between(start, end)
        val h = minutes / 60.0
        return (h * 100.0).roundToInt() / 100.0
    }
}