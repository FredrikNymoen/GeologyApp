package org.example.ui.worker

import org.example.services.WorkerService
import org.example.ui.common.ConsoleIO

class AddWorkerMenu (
    private val workerService: WorkerService
){

    fun run() {
        println("\n=== Add Worker ===")
        val firstName = ConsoleIO.nonEmpty("First name")
        val lastName  = ConsoleIO.nonEmpty("Last name")
        val phone     = ConsoleIO.nonEmpty("Phone")

        try {
            // Auto-generates workerId inside WorkerService
            val w = workerService.create(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                phone = phone.trim()
            )
            println("Worker '${w.workerId}' added: ${w.getFullName()}")
        } catch (e: IllegalArgumentException) {
            println("${e.message}")
        }

    }

}