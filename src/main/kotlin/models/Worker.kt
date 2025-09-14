package org.example.models

class Worker (
    var firstname:String? = null,
    var lastname:String? = null,
    var age:Int? = null,
    var experienceYears:Int? = null
) {

    override fun toString(): String =
        "First Name: ${firstname ?: "(unknown)"} | " +
        "Last Name: ${lastname ?: "(unknown)"} | " +
        "Age: ${age ?: "(unknown)"} | " +
        "Experience Years: ${experienceYears ?: "(unknown)"}"
}