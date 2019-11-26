package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R

//TODO: look into making this class a fragment
class HeadphonesActivity(private val volumeMap : MutableMap<String, Int>) : Fragment() {

    private val TAG = "HeadphonesActivity"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.headphones, container, false)
    }
}
