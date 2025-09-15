package org.example.actions.mineral

import org.example.actions.MenuAction

enum class AddMineralMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    SelectExisting ("Select Existing Mineral", "1"),
    CreateNew ("Create New Mineral", "2"),
    Cancel ("Cancel", "C"),
}