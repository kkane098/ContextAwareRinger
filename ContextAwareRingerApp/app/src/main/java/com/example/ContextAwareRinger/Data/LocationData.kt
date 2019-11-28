package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

@Serializable
data class LocationData(
    val name: String,
    val placeName: String,
    val lat: Double,
    val lng: Double,
    val radius: Double,
    val fenceKey: String,
    val ringerMode: Int
){
    override fun equals(other: Any?): Boolean {
        val ot = other as LocationData
        if((ot.fenceKey == this.fenceKey) && (ot.name == this.name) && (ot.ringerMode == this.ringerMode) && (ot.placeName == this.placeName)&& (ot.lat == this.lat) && (ot.lng == this.lng) && (ot.radius == this.radius)){
            return true
        }
        return false
    }

}