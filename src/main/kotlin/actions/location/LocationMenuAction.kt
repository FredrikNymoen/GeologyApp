package org.example.actions.location

import org.example.actions.MenuAction

/**
 * Represents possible user actions in the location menu.
 * Each action has a label and a shortcut key.
 */
enum class LocationMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all locations", "1"),
    ListWorkers("List workers at location", "2"),
    ListMinerals("List minerals at location", "3"),
    Add("Add location", "4"),
    Update("Update location", "5"),
    Delete("Delete location", "6"),
    Back("Go back", "B");
}