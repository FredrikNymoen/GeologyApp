package org.example.ui.common

object ConsoleIO {

    fun showMenu(title: String, items: List<String>) {
        println("\n=== $title ===")
        items.forEach { println(it) }
        print("Choose: ")
    }

    fun choice(): String = readln().trim().lowercase() //waits on enter


    /** Prompt once and return trimmed string. */
    fun prompt(label: String): String {
        print("$label: ")
        return choice()
    }

    /** Keep asking until non-empty. */
    fun nonEmpty(label: String): String {
        while (true) {
            val s = prompt(label)
            if (s.isNotBlank()) return s
            println("Please enter a non-empty value.")
        }
    }

    /** Return null if blank. */
    fun optional(label: String): String? =
        prompt(label).ifBlank { null }

    /** Accept both '.' and ',' decimals. */
    private fun parseDoubleLoose(raw: String): Double? =
        raw.replace(',', '.').toDoubleOrNull()

    /** Keep asking until valid Double in range [min, max]. */
    fun doubleInRange(label: String, min: Double, max: Double): Double {
        while (true) {
            val v = parseDoubleLoose(prompt(label))
            if (v != null && v in min..max) return v
            println("Please enter a number between $min and $max.")
        }
    }

    /** Optional double in range: blank = keep current (returns null). */
    fun doubleInRangeOrKeep(label: String, min: Double, max: Double): Double? {
        while (true) {
            val raw = prompt(label)
            if (raw.isBlank()) return null
            val v = parseDoubleLoose(raw)
            if (v != null && v in min..max) return v
            println("Please enter a number between $min and $max, or press Enter to keep current.")
        }
    }

    /** Parse comma or slash separated list ("" -> emptyList). */
    fun parseList(input: String): List<String> =
        input.split(Regex("[,/]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    /** Prompt and parse list (blank -> emptyList). */
    fun promptList(label: String): List<String> =
        parseList(prompt(label))
}