package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*

val ACTIVITY_FOOT = 0
val ACTIVITY_VEHICLE = 1
val ACTIVITY_BICYCLE = 2

val ACTIVITY_FOOT_KEY = "activity_foot"
val ACTIVITY_VEHICLE_KEY = "activity_vehicle"
val ACTIVITY_BICYCLE_KEY = "activity_bicycle"

@Serializable
data class ActivityData(val activityType: Int, val fenceKey: String, val ringerMode: Int)