package org.example

import org.example.ui.App
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

/**
 * Application entry point.
 *
 * Boots the console application by constructing [App] and running its main loop.
 * A "last-chance" try/catch prevents silent crashes and prints diagnostics
 * to standard error to aid debugging.
 *
 * @see App
 */
fun main() {
    try {
        App().run()
    } catch (t: Throwable) {
        println("Fatal error: ${t.message}")
        t.printStackTrace()
    }
}