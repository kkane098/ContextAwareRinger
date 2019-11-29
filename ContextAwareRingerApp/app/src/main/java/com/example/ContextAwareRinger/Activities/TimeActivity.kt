package com.example.ContextAwareRinger.Activities;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.TimeDataListAdapter
import com.example.ContextAwareRinger.Data.TimeData
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeActivity() : Fragment() {
    val TAG = "Time Activity"
    var floatingActionButton : FloatingActionButton? = null
    lateinit var timeDataList : MutableList<TimeData>

    internal lateinit var  timeListView: ListView
    internal lateinit var timeAdapter: TimeDataListAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.time, container, false)
        floatingActionButton = rootView.findViewById(R.id.timeFAB) as FloatingActionButton

        val timeDataList = readTimeDataList(context!!, TIME_LIST_FILENAME).toMutableList()
        val volumeMap = readVolumeMap(context!!, VOLUME_MAP_FILENAME).toMutableMap()

        timeListView = rootView.findViewById(R.id.ListViewTimes) as ListView
        timeAdapter = TimeDataListAdapter(context!!)
        timeListView.adapter = timeAdapter

        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            Log.i(TAG, "Adding onclick listener to fab")
            val ft = fragmentManager!!.beginTransaction()
            val dialogFragment = TimeDialogFragment(timeDataList, volumeMap, timeAdapter)
            dialogFragment.show(ft, "dialog")

        }

        timeListView?.setOnItemLongClickListener { parent, view, position, id ->
            Log.i(TAG, "Adding onLongClick listener to list view items")
            var time = timeAdapter.getItem(position) as TimeData
            val ft = fragmentManager!!.beginTransaction()
            val dialogFragment = TimeUpdateDeleteDialogFragment(timeDataList, volumeMap, timeAdapter, time, position)
            dialogFragment.show(ft, "dialog")
            true
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        if(timeAdapter.count == 0){
            val timeList = readTimeDataList(context!!, TIME_LIST_FILENAME)
            for (i in timeList){
                timeAdapter.add(i)
            }
        }
    }
}
