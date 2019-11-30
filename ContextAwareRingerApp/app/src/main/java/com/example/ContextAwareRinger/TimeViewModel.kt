package com.example.ContextAwareRinger

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimeViewModel : ViewModel() {
    var hour = MutableLiveData<Int>()
    var minute = MutableLiveData<Int>()
    var name = MutableLiveData<String>()
}