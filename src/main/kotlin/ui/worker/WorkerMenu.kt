package org.example.ui.worker

import org.example.actions.worker.WorkerMenuAction
import org.example.services.LocationService
import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO
import org.example.utils.fromInput

/**
 * Console-driven worker menu: lists/sorts workers, adds/updates/deletes, and
 * calculates typical monthly pay (weekly total × 52/12). Re-prompts on bad input.
 */
class WorkerMenu (
    private val workerService: WorkerService,
    locationService: LocationService
){

    private val addWorkerMenu = AddWorkerMenu(workerService)
    private val updateWorkerMenu = UpdateWorkerMenu(workerService, locationService)
    private val options = WorkerMenuAction.entries.map { "${it.shortcut} - ${it.label}" }

    /** Main loop; returns when user chooses Back. */
    fun run() {
            while (true) {
                // Show menu and read choice
                ConsoleIO.showMenu("Worker Menu", options)
                val choice = ConsoleIO.choice()

                val action = fromInput<WorkerMenuAction>(choice)
                if (action == null) {
                    println("Invalid choice. Please try again.")
                    continue
                }

                when (action) {

                    // 1) List all
                    WorkerMenuAction.ListAll -> {
                        val all = workerService.getAll()
                        if (all.isEmpty()) println("No workers found.")
                        else all.forEachIndexed { i, m -> println("${i + 1}. $m") } // uses Worker.toString()
                    }

                    // 2) Sort by last name
                    WorkerMenuAction.SortByLastName -> {
                        val sorted = workerService.getAll().sortedBy { it.lastName }
                        if (sorted.isEmpty()) println("No workers found.")
                        else sorted.forEachIndexed { i, m -> println("${i + 1}. $m") }
                    }

                    // 3) Add worker
                    WorkerMenuAction.Add -> {
                        addWorkerMenu.run()
                    }

                    // 4) Update worker
                    WorkerMenuAction.Update -> {
                        updateWorkerMenu.run()
                    }

                    // 5) Delete worker
                    WorkerMenuAction.Delete -> {
                        print("Enter worker id to delete: ")
                        val workerId = ConsoleIO.choice()
                        val success = workerService.delete(workerId)
                        if (success) println("Worker deleted.")
                        else println("Worker not found.")
                    }

                    // 6) Calculate monthly salary for all workers
                    WorkerMenuAction.CalculatePaychecksForAll -> {
                        val paychecks = workerService.calculatePaychecksForAllTypical()
                        if (paychecks.isEmpty()) {
                            println("No workers found.")
                        } else {
                            println("\nMonthly paychecks (avg, based on weekly schedule × 52/12):")
                            paychecks.forEach { (worker, amount) ->
                                val name = "${worker.firstName} ${worker.lastName}".trim()
                                println(" - ${worker.workerId} | $name : ${"%.2f".format(amount)}")
                            }
                        }
                    }

                    // 7) Calculate monthly salary for a specific worker
                    WorkerMenuAction.CalculatePaycheckForWorker -> {
                        val workerId = ConsoleIO.nonEmpty("Enter worker id")
                        val paycheck = workerService.calculatePaycheckForWorkerTypical(workerId)
                        if (paycheck == null) {
                            println("Worker not found.")
                        } else {
                            println("Monthly paycheck (avg, weekly × 52/12) for '${workerService.get(workerId)?.getFullName()}': ${"%.2f".format(paycheck)}")
                        }
                    }

                    // B) Go back
                    WorkerMenuAction.Back -> return
                }
            }
        }
}