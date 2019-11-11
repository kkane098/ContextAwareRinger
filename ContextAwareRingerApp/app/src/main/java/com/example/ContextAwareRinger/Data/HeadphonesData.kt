package com.example.ContextAwareRinger.Data

import java.io.Serializable

data class HeadphonesData(val headphoneState: Int, val fenceKey: String, val ringerMode: Int) :
    Serializable