package org.example.actions.mineral

import org.example.actions.MenuAction

/**
 * Actions available in the filter menu for minerals.
 */
enum class FilterMenuAction(
    override val label: String,
    override val shortcut: String) : MenuAction {
    SetName("Set nameContains", "1"),
    SetColor("Set color (exact)", "2"),
    SetFracture("Set fracture (exact)", "3"),
    SetHardness("Set hardness value (e.g. 6.8)", "4"),
    RunFilter("Run filter", "5"),
    Clear("Clear all filters", "6"),
    Back("Go back", "B");
}