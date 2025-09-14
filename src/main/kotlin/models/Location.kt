package org.example.models

class Location (
    var name:String? = null,
    var description:String? = null,
    var latitude:Double, //number between -90 and 90
    var longitude:Double, //number between -180 and 180
    private var minerals:MutableList<Mineral>? = mutableListOf(),
    private var workers:MutableList<Worker>? = mutableListOf()
) {
    override fun toString(): String =
        "Name: ${name} | " +
        "Description: ${description} | " +
        "Latitude: ${latitude} | " +
        "Longitude: ${longitude} | " +
        "Minerals: ${minerals?.joinToString { it.name ?: "(unknown)" }} | " +
        "Workers: ${workers?.joinToString { it.firstname + " " + it.lastname }}"

    fun getMinerals(): String =
        minerals
            ?.takeIf { it.isNotEmpty() }
            ?.joinToString(separator = "\n") { it.toString() }
            ?: "(none)"

    fun addMineral(mineral: Mineral) {
        minerals?.add(mineral)
    }


}