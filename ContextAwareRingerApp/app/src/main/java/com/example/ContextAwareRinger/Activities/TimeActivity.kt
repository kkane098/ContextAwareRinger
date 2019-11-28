package com.example.ContextAwareRinger.Activities;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.*
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeActivity : Fragment() {
    val TAG = "Time Activity"
    var floatingActionButton : FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.time, container, false)
        floatingActionButton = rootView.findViewById(R.id.timeFAB)

        val timeDataList = readTimeDataList(context!!, TIME_LIST_FILENAME).toMutableList()
        val volumeMap = readVolumeMap(context!!, VOLUME_MAP_FILENAME).toMutableMap()

        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            Log.i(TAG, "Adding onclick listener to fab")
            val ft = fragmentManager!!.beginTransaction()
            val dialogFragment = TimeDialogFragment(timeDataList, volumeMap)
            dialogFragment.show(ft, "dialog")
        }

        return rootView
    }
}
