package org.example.utils

import org.example.actions.MenuAction

/**
 * Maps raw user input (e.g. "1", "b") to the enum constant of type [T] whose
 * [MenuAction.shortcut] matches (case-insensitive). Returns null on no match.
 *
 * - `inline` + `reified T` lets us call `enumValues<T>()` without passing a Class.
 * - Constrained so [T] must be both an Enum and implement [MenuAction].
 */
inline fun <reified T> fromInput(input: String): T?
        where T : Enum<T>, T : MenuAction =
    enumValues<T>().firstOrNull { it.shortcut.equals(input, ignoreCase = true) }
