package com.example.ContextAwareRinger.Data

import java.io.Serializable

data class TimeData(
    val hour: Int,
    val min: Int,
    val day: Int,
    val workKey: String,
    val ringerMode: Int
) : Serializable