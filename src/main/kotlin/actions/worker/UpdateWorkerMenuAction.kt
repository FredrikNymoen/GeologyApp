package org.example.actions.worker

import org.example.actions.MenuAction

/**
 * Actions for the Update Worker Menu.
 * Each action has a label and a shortcut key.
 */
enum class UpdateWorkerMenuAction(
    override val label: String,
    override val shortcut: String
) : MenuAction {
    EditWorkerInformation("Edit worker information", "1"),
    AddOrReplaceShift("Add/Replace shift for a weekday", "2"),
    RemoveShift("Remove shift for a weekday", "3"),
    ListShifts("List shifts", "4"),
    Back("Go back", "B");
}