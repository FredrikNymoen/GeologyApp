package org.example.utils

import org.example.actions.MenuAction

// Generic helper function to map user input (like "1" or "B")
// to the correct enum value in a menu.
// inline: inlines the function code instead of making a normal call → required to use reified
// reified: lets you use the type T as if it were known at runtime (can check type, call enumValues, etc.)
inline fun <reified T> fromInput(input: String): T?
        // Constrain T so it must be both an Enum and implement MenuAction
        where T : Enum<T>, T : MenuAction {

    // Get all enum values of type T, then find the first one
    // where the shortcut matches the user input (case-insensitive).
    // If no match is found, return null.
    return enumValues<T>().firstOrNull {
        it.shortcut.equals(input, ignoreCase = true)
    }
}