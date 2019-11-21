package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

@Serializable
data class ActivityData(val activityType: Int, val fenceKey: String, val ringerMode: Int)