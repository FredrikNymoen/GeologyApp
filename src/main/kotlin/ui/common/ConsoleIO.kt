package org.example.ui.common

/**
 * Console input/output helper functions.
 */
object ConsoleIO {

    /** Show a menu with title and items, then prompt for choice. */
    fun showMenu(title: String, items: List<String>) {
        println("\n=== $title ===")
        items.forEach { println(it) }
        print("Choose: ")
    }

    /** Read line, trim whitespace, convert to lowercase. */
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


    /** Parse comma or slash separated list ("" -> emptyList). */
    fun parseList(input: String): List<String> =
        input.split(Regex("[,/]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

}