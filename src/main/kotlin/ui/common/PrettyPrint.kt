package org.example.ui.common

import org.example.models.Location
import org.example.models.Mineral
import org.example.models.Worker
import java.time.format.DateTimeFormatter
import kotlin.math.abs

object PrettyPrint {

    private val timeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")

    // --- configurable column widths for mineral ---
    data class LabeledCols(
        val name: Int = 17,
        val luster: Int = 34,
        val color: Int = 22,
        val hardness: Int = 19,
        val fracture: Int = 27
    )

    private fun padRight(s: String, w: Int): String =
        if (s.length >= w) s else s + " ".repeat(w - s.length)

    private fun ellipsize(s: String, w: Int): String =
        if (w <= 1) "…" else if (s.length <= w) s else s.take(w - 1) + "…"

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

    /** Lager én kolonne med "Label: value", og passer på bredde + trunkering. */
    private fun labeledCol(label: String, value: String, totalWidth: Int): String {
        val prefix = "$label "
        val avail = (totalWidth - prefix.length).coerceAtLeast(1)
        val body  = prefix + ellipsize(value, avail)
        return padRight(body, totalWidth)
    }

    /** Én rad per mineral, med label i hver kolonne og faste bredder. */
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



    fun location(loc: Location): String = buildString {
        appendLine("Location")
        appendLine("  ID        : ${loc.locationId}")
        appendLine("  Name      : ${loc.name ?: "(unnamed)"}")
        appendLine("  Description: ${loc.description ?: "(none)"}")
        appendLine("  Latitude  : ${loc.latitude}")
        appendLine("  Longitude : ${loc.longitude}")
        appendLine("  Minerals  (${loc.getMinerals().size}):")
        appendLine(formatList(loc.getMinerals()) { it.name ?: it.toString() })
        appendLine("  Workers   (${loc.getWorkers().size}):")
        appendLine(formatList(loc.getWorkers()) { "${it.firstName} ${it.lastName}".trim() })
    }

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