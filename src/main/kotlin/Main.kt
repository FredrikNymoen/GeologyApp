package org.example

import org.example.services.LocationService
import org.example.services.MineralService
import org.example.ui.LocationMenu
import org.example.ui.MainMenu
import org.example.ui.MenuAction
import org.example.ui.MineralMenu
import org.example.utils.MineralLoader

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    val mineralService = MineralService()
    val locationService = LocationService()

    val mainMenu = MainMenu()
    val mineralMenu = MineralMenu(mineralService)
    val locationMenu = LocationMenu(locationService)

    while (true) {
        when (mainMenu.showAndRead()){
            MenuAction.Locations -> locationMenu.run()
            MenuAction.Minerals -> mineralMenu.run()
            MenuAction.Workers -> println("Workers")
            MenuAction.UpdateWorkers -> println("Update workers")
            MenuAction.Exit -> return
        }
    }



}