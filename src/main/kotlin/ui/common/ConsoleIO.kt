package org.example.ui.common

object ConsoleIO {

    fun showMenu(title: String, items: List<String>) {
        println("\n=== $title ===")
        items.forEach { println(it) }
        print("Choose: ")
    }

    fun choice(): String = readln().trim().lowercase() //waits on enter

}