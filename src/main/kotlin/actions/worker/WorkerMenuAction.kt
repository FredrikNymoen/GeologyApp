package org.example.actions.worker

import org.example.actions.MenuAction

enum class WorkerMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all workers", "1"),
    Add("Add worker", "2"), // which location(s) they work at, name and employee ID, phone number, what days and time they work, and hourly wage.
    Update("Update worker", "3"),
    Delete("Delete worker", "4"),
    Back("Go back", "B");
}