package org.example.utils

import org.example.models.Location

object LocationLoader {
    fun loadFromFile(): List<Location> {
        val out = mutableListOf<Location>()
        val input = javaClass.getResourceAsStream("/locations.txt") ?: return emptyList()

        val splitter = Regex("\\t+|\\s{2,}") // tab eller 2+ space
        input.bufferedReader().useLines { lines ->
            lines
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
                // .drop(1)  // fjern denne
                .forEach { line ->
                    val p = line.split(splitter)
                    if (p.size >= 5) {
                        val id   = p[0].trim()
                        val name = p[1].trim().ifBlank { null }
                        val desc = p[2].trim().ifBlank { null }
                        val lat  = p[3].trim().toDouble()
                        val lon  = p[4].trim().toDouble()
                        out += Location(id, name, desc, lat, lon)
                    }
                }
        }
        return out
    }
}
