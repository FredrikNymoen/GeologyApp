package org.example.actions.location

import org.example.actions.MenuAction

enum class LocationMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all locations", "1"),
    ListMinerals("List minerals at location", "2"),
    Add("Add location", "3"),
    Update("Update location", "4"),
    Delete("Delete location", "5"),
    AddMineral("Add mineral to location", "6"),
    AddWorker("Add worker to location", "7"),
    Back("Go back", "B");
}