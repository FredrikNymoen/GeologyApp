package org.example.actions.worker

import org.example.actions.MenuAction

/**
 * Actions available in the Worker Menu.
 * Each action has a user-friendly label and a shortcut key for selection.
 */
enum class WorkerMenuAction (
    override val label: String,
    override val shortcut: String
) : MenuAction {
    ListAll("List all workers", "1"),
    SortByLastName("Sort workers by last name", "2"),
    Add("Add worker", "3"), // which location(s) they work at, name and employee ID, phone number, what days and time they work, and hourly wage.
    Update("Update worker", "4"),
    Delete("Delete worker", "5"),
    CalculatePaychecksForAll("Calculate monthly paychecks for all workers", "6"),
    CalculatePaycheckForWorker("Calculate monthly paycheck for a specific worker", "7"),
    Back("Go back", "B");
}