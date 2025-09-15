package org.example.models

import org.example.ui.common.PrettyPrint

class Worker (
    val workerId: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    private val shifts: MutableList<WorkShift> = mutableListOf()
) {

    override fun toString(): String {
        return PrettyPrint.worker(this)
    }

    fun getFullName(): String = "$firstName $lastName"


    fun addShift(shift: WorkShift) {
        shifts.add(shift)
    }

    fun replaceShift(oldShiftIndex: Int, newShift: WorkShift) {
        if (oldShiftIndex in shifts.indices) {
            shifts[oldShiftIndex] = newShift
        } else {
            throw IndexOutOfBoundsException("No shift found at index $oldShiftIndex")
        }
    }

    fun removeShift(shift: WorkShift) : Boolean {
        return shifts.remove(shift)
    }

    fun getShifts(): List<WorkShift> = shifts.toList()
}