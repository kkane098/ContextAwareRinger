package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R


class ActivitiesActivity(private val volumeMap: MutableMap<String, Int>) : Fragment(){
    //TODO: look into making this class a fragment
    //TODO: Add plus button to UI
    //TODO: Change listview to cardview
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.activity, container, false)
    }
}
