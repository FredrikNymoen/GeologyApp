package org.example.actions.location

import org.example.actions.MenuAction

enum class LocationMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all locations", "1"),
    Add("Add location", "2"),
    Update("Update location", "3"),
    Delete("Delete location", "4"),
    Back("Go back", "B");
}