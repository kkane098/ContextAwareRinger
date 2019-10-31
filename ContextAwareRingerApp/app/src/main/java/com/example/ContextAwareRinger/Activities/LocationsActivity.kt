package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle;
import com.example.myapplication.R;

class LocationsActivity : Activity(){
    //TODO: look into making this class a fragment
    //TODO: look into location picker
    //TODO: implement settings dialog
    //TODO: Update UI to cardview
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location)
    }
}
