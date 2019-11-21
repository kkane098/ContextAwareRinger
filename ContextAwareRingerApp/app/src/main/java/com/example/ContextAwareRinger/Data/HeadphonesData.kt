package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

@Serializable
data class HeadphonesData(val headphoneState: Int, val fenceKey: String, val ringerMode: Int)