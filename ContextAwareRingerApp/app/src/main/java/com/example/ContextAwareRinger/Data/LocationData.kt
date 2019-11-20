package com.example.ContextAwareRinger.Data

import java.io.Serializable

data class LocationData(
    val lat: Double,
    val lng: Double,
    val radius: Double,
    val fenceKey: String,
    val ringerMode: Int
) : Serializable