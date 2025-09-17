package org.example.utils

import org.example.models.Mineral

/**
 * Loads minerals from a classpath file (default `/minerals.txt`).
 * Columns: name, luster, color, hardness, fracture. Delimiter: tabs or 2+ spaces.
 * `luster`/`color` split on `/` or `,`. `hardness` may be blank, single value, or `min-max`.
 * Ignores blank lines, comments (`#`), and a header row.
 */
object MineralLoader {

    /** Split on tabs or runs of 2+ spaces. */
    private val splitter = Regex("\\t+|\\s{2,}")

    /** Split list-like fields on `/` or `,`. */
    private val listSplitter = Regex("[/,]")

    fun loadFromFile(resourcePath: String = "/minerals.txt"): List<Mineral> {
        val out = mutableListOf<Mineral>()
        val input = javaClass.getResourceAsStream(resourcePath) ?: return emptyList()

        input.bufferedReader().useLines { lines ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                .forEach { line ->
                    val parts = line.split(splitter)
                    if (parts.size < 5) {
                        System.err.println("MineralLoader: skipping line (needs 5+ columns): '$line'")
                        return@forEach
                    }

                    // Skip header (e.g., "Name  Luster  Color  Hardness  Fracture")
                    if (parts[0].equals("name", true)) return@forEach

                    val name = parts[0].ifBlank { null }

                    // Parse multi-valued fields
                    val luster = parts[1].split(listSplitter).map { it.trim() }.filter { it.isNotEmpty() }
                    val color  = parts[2].split(listSplitter).map { it.trim() }.filter { it.isNotEmpty() }

                    // Parse hardness: blank | single | range "a-b"
                    val hardnessStr = parts[3].trim()
                    var min: Double? = null
                    var max: Double? = null
                    if (hardnessStr.isNotBlank()) {
                        if ('-' in hardnessStr) {
                            val (aRaw, bRaw) = hardnessStr.split('-', limit = 2)
                            val a = aRaw.trim().replace(',', '.').toDoubleOrNull()
                            val b = bRaw.trim().replace(',', '.').toDoubleOrNull()
                            if (a != null && b != null) { min = a; max = b }
                        } else {
                            val v = hardnessStr.replace(',', '.').toDoubleOrNull()
                            if (v != null) { min = v; max = v }
                        }
                    }

                    val fracture = parts[4].ifBlank { null }

                    out += Mineral(
                        name        = name,
                        luster      = luster,
                        color       = color,
                        hardnessMin = min,
                        hardnessMax = max,
                        fracture    = fracture
                    )
                }
        }
        return out
    }
}
