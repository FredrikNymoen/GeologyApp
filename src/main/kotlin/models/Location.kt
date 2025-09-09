package org.example.models

class Location (
    var name:String,
    var description:String,
    var latitude:Double, //number between -90 and 90
    var longitude:Double, //number between -180 and 180
    var minerals:List<Mineral>
)