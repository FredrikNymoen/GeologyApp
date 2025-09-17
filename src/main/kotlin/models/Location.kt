package org.example.models

import org.example.ui.common.PrettyPrint

/**
 * Represents a geological location with associated minerals and workers.
 *
 * @property locationId Unique identifier for the location.
 * @property name Name of the location.
 * @property description Optional description of the location.
 * @property latitude Latitude of the location (between -90 and 90).
 * @property longitude Longitude of the location (between -180 and 180).
 */
class Location (
    var locationId: String,
    var name:String? = null,
    var description:String? = null,
    var latitude:Double, //number between -90 and 90
    var longitude:Double, //number between -180 and 180
) {
    private var minerals:MutableList<Mineral> = mutableListOf()
    private var workers:MutableList<Worker> = mutableListOf()


    /**
     * Provides a string representation of the location using PrettyPrint.
     */
    override fun toString(): String{
        return PrettyPrint.location(this)
    }

    /**
     * Returns a list of minerals associated with the location.
     */
    fun getMinerals(): List<Mineral> =
        minerals.toList()

    /**
     * Adds a mineral to the location.
     */
    fun addMineral(mineral: Mineral) {
        minerals.add(mineral)
    }

    /**
     * Removes a mineral from the location.
     */
    fun removeMineral(mineral: Mineral) {
        minerals.remove(mineral)
    }

    /**
     * Returns a list of workers associated with the location.
     */
    fun getWorkers(): List<Worker> =
        workers.toList()

    /**
     * Adds a worker to the location.
     */
    fun addWorker(worker: Worker) {
        workers.add(worker)
    }
}