package org.example.actions.location

import org.example.actions.MenuAction

enum class UpdateLocationMenuAction(
    override val label: String,
    override val shortcut: String
) : MenuAction {
    EditInfo("Edit name/description/coordinates", "1"),
    AddMineral("Add mineral to location", "2"),
    RemoveMineral("Remove mineral by name", "3"),
    Back("Go back", "B");
}