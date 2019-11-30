package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.ACTIVITY_LIST_FILENAME
import com.example.ContextAwareRinger.Data.ActivityData
import com.example.ContextAwareRinger.R
import com.example.ContextAwareRinger.readActivityDataList


class ActivitiesActivity(private val volumeMap: MutableMap<String, Int>) : Fragment(){

    private val TAG = "ActivitiesActivity"
    lateinit var mActivitiesDataList: MutableList<ActivityData>

    //TODO: look into making this class a fragment
    //TODO: Add plus button to UI
    //TODO: Change listview to cardview
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.activity, container, false)

        mActivitiesDataList = readActivityDataList(context!!, ACTIVITY_LIST_FILENAME).toMutableList()

        if(mActivitiesDataList.size == 0){

        }

        return rootView
    }
}
