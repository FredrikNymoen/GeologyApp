package org.example.utils

import org.example.models.Mineral

object MineralLoader { //only need one instance, therefore object
    fun loadFromFile(): List<Mineral> {
        val minerals = mutableListOf<Mineral>()
        val inputStream = javaClass.getResourceAsStream("/minerals.txt") ?: return emptyList()

        val splitter = Regex("\\t+|\\s{2,}") // tab or 2+ space
        inputStream.bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->  // drop header row
                val parts = line.split(splitter)
                if (parts.size >= 5) {
                    val name = parts[0].ifBlank { null }
                    // Split by "/" to allow multiple values, trim spaces, and remove empty entries
                    val luster = parts[1].split("/").map { it.trim() }.filter { it.isNotEmpty() }
                    val color = parts[2].split("/").map { it.trim() }.filter { it.isNotEmpty() }

                    // Hardness
                    val hardnessStr = parts[3].trim()
                    var min: Double? = null
                    var max: Double? = null
                    if (hardnessStr.isNotBlank()) {
                        if ("-" in hardnessStr) {
                            val (a, b) = hardnessStr.split("-", limit = 2).map { it.toDouble() }
                            min = a
                            max = b
                        } else {
                            val v = hardnessStr.toDouble()
                            min = v
                            max = v
                        }
                    }

                    val fracture = parts[4].ifBlank { null }

                    minerals.add(
                        Mineral(
                            name = name,
                            luster = luster,
                            color = color,
                            hardnessMin = min,
                            hardnessMax = max,
                            fracture = fracture
                        )
                    )
                }
            }
        }
        return minerals
    }
}
