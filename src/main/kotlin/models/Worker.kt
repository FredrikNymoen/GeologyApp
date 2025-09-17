package org.example.models

import org.example.ui.common.PrettyPrint

/**
 * Data class representing a worker.
 * @property workerId Unique identifier for the worker.
 * @property firstName First name of the worker.
 * @property lastName Last name of the worker.
 * @property phone Contact phone number of the worker.
 * @property shifts List of work shifts assigned to the worker.
 */
class Worker (
    val workerId: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    private var shifts: MutableList<WorkShift> = mutableListOf()
) {

    /** Pretty print for displaying worker details. */
    override fun toString(): String {
        return PrettyPrint.worker(this)
    }

    /** Returns the full name of the worker. */
    fun getFullName(): String = "$firstName $lastName"

    /** Adds a new work shift to the worker's list of shifts. */
    fun addShift(shift: WorkShift) {
        shifts.add(shift)
    }

    /** Replaces an existing shift at the specified index with a new shift. */
    fun replaceShift(oldShiftIndex: Int, newShift: WorkShift) {
        if (oldShiftIndex in shifts.indices) {
            shifts[oldShiftIndex] = newShift
        } else {
            throw IndexOutOfBoundsException("No shift found at index $oldShiftIndex")
        }
    }

    /** Removes a specific shift from the worker's list of shifts. Returns true if removed, false if not found. */
    fun removeShift(shift: WorkShift) : Boolean {
        return shifts.remove(shift)
    }

    /** Gets the list of all work shifts assigned to the worker. */
    fun getShifts(): List<WorkShift> = shifts.toList()
}