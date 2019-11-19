package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R

class LocationsActivity : Fragment(){
    //TODO: look into making this class a fragment
    //TODO: look into location picker
    //TODO: implement settings dialog
    //TODO: Update UI to cardview
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.location, container, false)
    }
}
