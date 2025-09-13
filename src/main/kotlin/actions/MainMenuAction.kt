package org.example.actions

enum class MainMenuAction(
    override val label: String,
    override val shortcut: String
) : MenuAction {
    Locations("See geological location data", "1"),
    Minerals("See mineral data", "2"),
    Workers("See worker data", "3"),
    UpdateWorkers("Update worker data", "4"),
    Exit("Shut down application", "Q");
}