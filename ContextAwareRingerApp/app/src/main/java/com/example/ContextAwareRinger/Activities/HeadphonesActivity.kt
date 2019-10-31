package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle
import com.example.myapplication.R

//TODO: look into making this class a fragment
class HeadphonesActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.headphones)
    }
}
