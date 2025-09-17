package org.example.utils

import org.example.models.Location

/**
 * Loads [Location] entities from a classpath resource (default: `/locations.txt`).
 *
 * ## Expected file format
 * - One location per line (blank lines and lines starting with `#` are ignored).
 * - Columns (5): `locationId  name  description  latitude  longitude`
 * - Delimiters: tabs **or** 2+ spaces (e.g., `"\\t+|\\s{2,}"`).
 * - `name` and `description` may be blank (treated as `null`).
 * - `latitude` ∈ [-90, 90], `longitude` ∈ [-180, 180].
 * - Decimal separators `.` or `,` are accepted.
 */
object LocationLoader {

    /** Regex that splits on tab(s) or runs of 2+ spaces. */
    private val splitter = Regex("\\t+|\\s{2,}")

    fun loadFromFile(resourcePath: String = "/locations.txt"): List<Location> {
        val out = mutableListOf<Location>()
        val input = javaClass.getResourceAsStream(resourcePath) ?: return emptyList()

        input.bufferedReader().useLines { lines ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val p = line.split(splitter)
                    if (p.size < 5) {
                        System.err.println("LocationLoader: skipping line (expected 5+ columns): '$line'")
                        return@forEach
                    }

                    val id   = p[0].trim()
                    val name = p[1].trim().ifBlank { null }
                    val desc = p[2].trim().ifBlank { null }

                    val lat = p[3].trim().replace(',', '.').toDoubleOrNull()
                    val lon = p[4].trim().replace(',', '.').toDoubleOrNull()
                    if (lat == null || lon == null) {
                        System.err.println("LocationLoader: skipping line (invalid lat/lon): '$line'")
                        return@forEach
                    }

                    out += Location(
                        locationId = id,
                        name = name,
                        description = desc,
                        latitude = lat,
                        longitude = lon
                    )
                }
        }
        return out
    }
}
