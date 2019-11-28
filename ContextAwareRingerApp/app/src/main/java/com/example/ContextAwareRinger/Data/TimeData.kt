package com.example.ContextAwareRinger.Data

import kotlinx.serialization.*
import java.sql.Time

@Serializable
data class TimeData(
    val hour: Int,
    val min: Int,
    val day: Int,
    val workKey: String,
    val ringerMode: Int
){
    override fun equals(other: Any?): Boolean {
        var ot = other as TimeData
        if((ot.hour == this.hour) && (ot.min == this.min) && (ot.day == this.day) && (ot.workKey == this.workKey) && (ot.ringerMode == this.ringerMode)){
            return true
        }
        return false
    }
}