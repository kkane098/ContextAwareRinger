package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

val ACTIVITY_VEHICLE = 0
val ACTIVITY_FOOT = 1
val ACTIVITY_BICYCLE = 2

@Serializable
data class ActivityData(val activityType: Int, val fenceKey: String, val ringerMode: Int)