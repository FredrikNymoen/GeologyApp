package org.example.actions

/**
 * Represents possible user confirmation actions.
 * Each action has a set of associated input keys (case-insensitive).
 * The companion object provides a method to map raw input to an action.
 */
enum class ConfirmationAction(val keys: Set<String>) {
    Save(setOf("y", "yes")),
    Edit(setOf("e", "edit")),
    Cancel(setOf("c", "cancel"));

    companion object {
        /**
         * Maps raw user input to the corresponding [ConfirmationAction].
         * Returns null if no matching action is found.
         *
         * @param input The raw user input string.
         * @return The matching [ConfirmationAction] or null if none matches.
         */
        fun from(input: String): ConfirmationAction? =
            entries.firstOrNull { it.keys.any { k -> k.equals(input, ignoreCase = true) } }
    }
}