package org.example.utils

object Input{  //only need one instance, therefore object
    fun showMenu(title: String, items: List<String>) {
        println("\n=== $title ===")
        items.forEach { println(it) }
        print("Choose: ")
    }
    fun choice(): String = readln().trim() //waits on enter

}