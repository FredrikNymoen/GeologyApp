package org.example.actions

// Confirmation choices for AddMineralMenu
enum class ConfirmationAction(val keys: Set<String>) {
    Save(setOf("y", "yes")),
    Edit(setOf("e", "edit")),
    Cancel(setOf("c", "cancel"));

    companion object {
        fun from(input: String): ConfirmationAction? =
            entries.firstOrNull { it.keys.any { k -> k.equals(input, ignoreCase = true) } }
    }
}