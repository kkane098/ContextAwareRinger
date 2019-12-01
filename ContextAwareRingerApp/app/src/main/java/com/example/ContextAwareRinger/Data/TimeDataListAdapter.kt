package com.example.ContextAwareRinger.Data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.ContextAwareRinger.DAILY
import com.example.ContextAwareRinger.R
import com.example.ContextAwareRinger.WEEKDAY
import com.example.ContextAwareRinger.WEEKEND
import java.util.*
import kotlin.collections.ArrayList

class TimeDataListAdapter (private val mContext: Context) : BaseAdapter(){
    private val mItems = ArrayList<TimeData>()

    fun add(item: TimeData){
        mItems.add(item)
        notifyDataSetChanged()
    }

    fun add(item: TimeData, index: Int) {
        mItems.add(index,item)
        notifyDataSetChanged()
    }

    fun clear(){
        mItems.clear()
        notifyDataSetChanged()
    }

    fun delete(item: TimeData){
        var count = 0
        for(i in mItems){
            if(i.equals(item)){
                break
            }
            count++
        }
        mItems.removeAt(count)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {

        return mItems.size

    }

    override fun getItem(pos: Int): Any {

        return mItems[pos]

    }

    override fun getItemId(pos: Int): Long {

        return pos.toLong()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //TODO("not implemented")

        val timeItem:TimeData = mItems.get(position)
        val newView : View
        val viewHolder: TimeDataListAdapter.ViewHolder

        if (null == convertView) {
            viewHolder = TimeDataListAdapter.ViewHolder()

            // TODO - Inflate the View for this ToDoItem
            newView = LayoutInflater.from(mContext).inflate(R.layout.time_item,parent,false)
            newView.tag = viewHolder
            viewHolder.mTimeView = newView.findViewById(R.id.textTime)
            viewHolder.mDayView = newView.findViewById(R.id.textTimeDay)
            viewHolder.mPreferenceView = newView.findViewById(R.id.textTimePreference)

        } else {
            viewHolder = convertView.tag as TimeDataListAdapter.ViewHolder

            newView = convertView
        }

        // TODO - Fill in specific ToDoItem data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        // TODO - Display Time in TextView
        var tempTime = ""

        // Convert time to 12 hour format
        var hr = timeItem.hour%12
        if(hr == 0){
            hr = 12
        }
        if(timeItem.min < 10){
            tempTime += (hr).toString() + ":0" + timeItem.min.toString() + " "
        }
        else{
            tempTime += (hr).toString() + ":" + timeItem.min.toString() + " "
        }

        if(timeItem.hour > 11){
            tempTime += "pm"
        }
        else{
            tempTime += "am"
        }

        // Display time
        viewHolder.mTimeView?.text = "Time: " + tempTime

        // Display Day in the view
        var tempDay = ""
        when(timeItem.day){
            DAILY -> tempDay = "Daily"
            WEEKDAY -> tempDay = "Weekdays"
            WEEKEND -> tempDay = "Weekend"
            Calendar.MONDAY -> tempDay = "Monday"
            Calendar.TUESDAY -> tempDay = "Tuesday"
            Calendar.WEDNESDAY -> tempDay = "Wednesday"
            Calendar.THURSDAY -> tempDay = "Thursday"
            Calendar.FRIDAY -> tempDay = "Friday"
            Calendar.SATURDAY -> tempDay = "Saturday"
            Calendar.SUNDAY -> tempDay = "Sunday"
        }

        viewHolder.mDayView?.text = "Choice of Day: ".plus(tempDay)

        // TODO - Display Priority in a TextView
        var ringer = ""
        if(timeItem.ringerMode == 0){
            ringer = "Silent"
        }
        else if(timeItem.ringerMode == 1){
            ringer = "Vibrate"
        }
        else{
            ringer = "Normal"
        }

        viewHolder.mPreferenceView?.text = "Ringer Preference:  ".plus(ringer)


        return newView
    }

    internal class ViewHolder {
        var position: Int = 0
        var mItemLayout: RelativeLayout? = null
        var mTimeView: TextView? = null
        var mDayView: TextView? = null
        var mPreferenceView: TextView? = null

    }
}