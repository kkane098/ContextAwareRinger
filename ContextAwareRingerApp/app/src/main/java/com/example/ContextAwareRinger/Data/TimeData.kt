package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

@Serializable
data class TimeData(
    val hour: Int,
    val min: Int,
    val day: Int,
    val workKey: String,
    val ringerMode: Int
)