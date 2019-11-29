package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

val HEADPHONES_IN = 0
val HEADPHONES_OUT = 1

@Serializable
data class HeadphonesData(val headphoneState: Int, val fenceKey: String, val ringerMode: Int)