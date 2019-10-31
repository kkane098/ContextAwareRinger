package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle
import com.example.myapplication.R

//TODO: Look into making this a fragment
class TimeActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.time)
    }
}
