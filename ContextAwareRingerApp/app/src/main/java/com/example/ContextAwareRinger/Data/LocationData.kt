package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

@Serializable
data class LocationData(
    val name: String,
    val lat: Double,
    val lng: Double,
    val radius: Double,
    val fenceKey: String,
    val ringerMode: Int
)