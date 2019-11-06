package com.example.ContextAwareRinger.Data

//TODO: Figure out how to handle different days of the week / combos
data class TimeData(val startTime: Long, val endTime: Long, val fenceKey: String, val ringerMode: Int)