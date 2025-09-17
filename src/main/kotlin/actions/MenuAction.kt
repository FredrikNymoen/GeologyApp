package org.example.actions

/**
 * Common interface for all menu actions.
 * Each action has a user-friendly label and a shortcut key.
 */
interface MenuAction {
    val label: String // e.g. "List all locations"
    val shortcut: String // e.g. "1" or "B"
}