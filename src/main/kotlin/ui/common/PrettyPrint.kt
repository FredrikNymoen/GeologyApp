package org.example.ui.common

import org.example.models.Location
import org.example.models.Mineral
import org.example.models.Worker
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/** Utility to pretty-print our data models in a human-readable way. */
object PrettyPrint {

    private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm") // 24h, no leading zero
    private fun Double.f2(): String = String.format("%.2f", this) // 2 decimals
    private fun String.cap3(): String = take(3).lowercase().replaceFirstChar { it.uppercase() } // Cap first letter, max 3 chars


    // --- configurable column widths for mineral ---
    data class LabeledCols(
        val name: Int = 17,
        val luster: Int = 34,
        val color: Int = 22,
        val hardness: Int = 19,
        val fracture: Int = 27
    )

    /** Pad or truncate to exactly width w. */
    private fun padRight(s: String, w: Int): String =
        if (s.length >= w) s else s + " ".repeat(w - s.length)

    /** Truncate string s to fit in width w, adding "…" if truncated. */
    private fun ellipsize(s: String, w: Int): String =
        if (w <= 1) "…" else if (s.length <= w) s else s.take(w - 1) + "…"

    /** Format hardness range nicely. */
    private fun fmtHardness(min: Double?, max: Double?): String {
        fun f(x: Double) = if (abs(x - x.toInt()) < 1e-9) "${x.toInt()}" else "%.1f".format(x)
        return when {
            min != null && max != null && min == max -> f(min)
            min != null && max != null               -> "${f(min)}–${f(max)}"
            min != null                               -> "≥${f(min)}"
            max != null                               -> "≤${f(max)}"
            else                                      -> "?"
        }
    }

    /** Format one labeled column with fixed total width, truncating value if needed. */
    private fun labeledCol(label: String, value: String, totalWidth: Int): String {
        val prefix = "$label "
        val avail = (totalWidth - prefix.length).coerceAtLeast(1)
        val body  = prefix + ellipsize(value, avail)
        return padRight(body, totalWidth)
    }

    /** One-line mineral with labeled fields, suitable for detailed view.
     * Uses fixed-width columns, truncating values if needed.
     * Default widths fit in 120 chars; customize via [cols].
     */
    fun mineralRowLabeled(
        m: Mineral,
        cols: LabeledCols = LabeledCols(),
        indexWidth: Int = 0          // width to pad the index to (e.g. list.size.toString().length)
    ): String {
        val name     = m.name ?: "(unknown)"
        val luster   = m.luster.joinToString("/")
        val color    = m.color.joinToString("/")
        val hardness = fmtHardness(m.hardnessMin, m.hardnessMax)
        val fracture = m.fracture ?: "(unknown)"


        return buildString {
            append(labeledCol("Name:",     name,     cols.name));     append("  ")
            append(labeledCol("Luster:",   luster,   cols.luster));   append("  ")
            append(labeledCol("Color:",    color,    cols.color));    append("  ")
            append(labeledCol("Hardness:", hardness, cols.hardness)); append("  ")
            append(labeledCol("Fracture:", fracture, cols.fracture))
        }
    }


    /** Compact 2–3 line worker preview used inside a Location printout. */
    fun workerCompactForLocation(worker: Worker, loc: Location, maxShifts: Int = 2): String = buildString {
        // Line 1: id | name | phone
        appendLine("${worker.workerId} | ${worker.getFullName()} | ${worker.phone}")

        // Filter only shifts at this location
        val here = worker.getShifts()
            .filter { it.location.locationId == loc.locationId }
            .sortedBy { it.day.value }

        // Line 2: shifts at this location (truncated)
        if (here.isEmpty()) {
            appendLine("  Shifts: (none)")
            append("  Weekly hours: 0.00")
            return@buildString
        }

        val shown = here.take(maxShifts).joinToString("; ") { s ->
            "${s.day.name.cap3()} ${s.start}–${s.end} @ ${"%.0f".format(s.hourlyWage)}/h"
        }
        val more = here.size - maxShifts
        appendLine(
            if (more > 0) "  Shifts: $shown (+$more more)"
            else          "  Shifts: $shown"
        )

        // Line 3: weekly hours at this location only
        val minutes = here.sumOf { ChronoUnit.MINUTES.between(it.start, it.end) }
        val hours = minutes / 60.0
        append("  Weekly hours here: ${hours.f2()}")
    }

    /** Detailed multi-line location view, including minerals and workers. */
    fun location(loc: Location): String = buildString {
        appendLine("Location")
        appendLine("  ID        : ${loc.locationId}")
        appendLine("  Name      : ${loc.name ?: "(unnamed)"}")
        appendLine("  Description: ${loc.description ?: "(none)"}")
        appendLine("  Latitude  : ${loc.latitude}")
        appendLine("  Longitude : ${loc.longitude}")
        appendLine("  Minerals  (${loc.getMinerals().size}):")
        appendLine(formatList(loc.getMinerals()) { it.name ?: it.toString() })

        val workers = loc.getWorkers()
        appendLine("  Workers   (${workers.size}):")
        if (workers.isEmpty()) {
            appendLine("    (none)")
        } else {
            val shown = workers.joinToString("\n") { w ->
                // hver worker blir maks 3 linjer
                "    " + workerCompactForLocation(w, loc).replace("\n", "\n    ")
            }
            appendLine(shown)
        }
    }

    /** Detailed multi-line worker view, including shifts. */
    fun worker(w: Worker): String = buildString {
        val shifts = w.getShifts().sortedBy { it.day.value }
        val weeklyHours = shifts.sumOf { it.hours() }

        appendLine("Worker")
        appendLine("  ID      : ${w.workerId}")
        appendLine("  Name    : ${w.getFullName()}")
        appendLine("  Phone   : ${w.phone}")
        appendLine("  Shifts  (${shifts.size}):")
        if (shifts.isEmpty()) {
            appendLine("    (none)")
        } else {
            shifts.forEach { s ->
                val loc = s.location
                val locLabel = "${loc.name ?: "(unnamed)"} [${loc.locationId}]"
                appendLine(
                    "    - ${s.day}: ${s.start.format(timeFmt)}–${s.end.format(timeFmt)} @ $locLabel — " +
                            "${"%.2f".format(s.hourlyWage)}/h (${String.format("%.2f", s.hours())} h)"
                )
            }
        }
        appendLine("  Weekly hours : ${"%.2f".format(weeklyHours)}")
    }

    /** Generic bullet list with truncation. */
    fun <T> formatList(items: List<T>, map: (T) -> String): String {
        if (items.isEmpty()) return "    (none)"
        return items.joinToString("\n") { "    - $it" }

    }
}