package org.example.actions.mineral

import org.example.actions.MenuAction

/**
 * Represents possible actions in the Mineral Menu.
 * Each action has a label and a shortcut key for user selection.
 */
enum class MineralMenuAction(
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all minerals", "1"),
    SortAZ("Sort all minerals A-Z", "2"),
    SearchByName("Search for mineral by name", "3"),
    Filter("Filter minerals", "4"),
    Add("Add mineral", "5"),
    Update("Update mineral", "6"),
    Delete("Delete mineral", "7"),
    Back("Go back", "B");
}