package org.example.models

import org.example.ui.common.PrettyPrint


class Location (
    var locationId: String,
    var name:String? = null,
    var description:String? = null,
    var latitude:Double, //number between -90 and 90
    var longitude:Double, //number between -180 and 180
) {
    private var minerals:MutableList<Mineral> = mutableListOf()
    private var workers:MutableList<Worker> = mutableListOf()


    override fun toString(): String{
        return PrettyPrint.location(this)
    }
    fun getMinerals(): List<Mineral> =
        minerals.toList()

    fun addMineral(mineral: Mineral) {
        minerals.add(mineral)
    }

    fun removeMineral(mineral: Mineral) {
        minerals.remove(mineral)
    }

    fun getWorkers(): List<Worker> =
        workers.toList()

    fun addWorker(worker: Worker) {
        workers.add(worker)
    }

    fun removeWorker(worker: Worker) {
        workers.remove(worker)
    }
}