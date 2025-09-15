package org.example.models

class Worker (
    val workerId: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    /** Timelønn som BigDecimal for presise penger-beregninger. */
    var hourlyWage: Double,
    /** Lokasjoner den ansatte er tilknyttet (navn/ID). */
    var locations: MutableSet<Location> = mutableSetOf(),
    /** Gjentakende ukentlige skift. */
    var shifts: MutableList<WorkShift> = mutableListOf()
) {
    fun fullName(): String = "$firstName $lastName"
}