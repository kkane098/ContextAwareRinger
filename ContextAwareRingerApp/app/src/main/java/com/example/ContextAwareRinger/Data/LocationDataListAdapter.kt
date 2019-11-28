package com.example.ContextAwareRinger.Data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.ContextAwareRinger.Activities.LocationsActivity
import com.example.ContextAwareRinger.R
import org.w3c.dom.Text
import java.util.ArrayList

class LocationDataListAdapter (private val mContext: Context) : BaseAdapter() {

    private val mItems = ArrayList<LocationData>()

    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed

    fun add(item: LocationData) {
        mItems.add(item)
        notifyDataSetChanged()
    }

    fun add(item: LocationData, index: Int) {
        mItems.add(index,item)
        notifyDataSetChanged()
    }

    // Clears the list adapter of all items.

    fun clear() {

        mItems.clear()

        notifyDataSetChanged()

    }

    fun delete(item: LocationData){
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

    fun update(item: LocationData){
        delete(item)
        add(item)
        notifyDataSetChanged()
    }

    // Returns the number of ToDoItems

    override fun getCount(): Int {

        return mItems.size

    }

    // Retrieve the number of ToDoItems

    override fun getItem(pos: Int): Any {

        return mItems[pos]

    }

    // Get the ID for the ToDoItem
    // In this case it's just the position

    override fun getItemId(pos: Int): Long {

        return pos.toLong()

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        //TODO("not implemented")

        val locationItem:LocationData = mItems.get(position)
        val newView : View
        val viewHolder: ViewHolder

        if (null == convertView) {
            viewHolder = ViewHolder()

            // TODO - Inflate the View for this ToDoItem
            newView = LayoutInflater.from(mContext).inflate(R.layout.location_item,parent,false)
            newView.tag = viewHolder
            viewHolder.mTitleView = newView.findViewById(R.id.textLocationName)
            viewHolder.mPlaceNameView = newView.findViewById(R.id.textPlaceName)
            viewHolder.mPriorityView = newView.findViewById(R.id.textLocationSubtitle)

        } else {
            viewHolder = convertView.tag as ViewHolder

            newView = convertView
        }

        // TODO - Fill in specific ToDoItem data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        // TODO - Display Title in TextView
        viewHolder.mTitleView?.text = locationItem.name

        viewHolder.mPlaceNameView?.text = locationItem.placeName

        // TODO - Display Priority in a TextView
        var ringer = ""
        if(locationItem.ringerMode == 0){
            ringer = "Silent"
        }
        else if(locationItem.ringerMode == 1){
            ringer = "Vibrate"
        }
        else{
            ringer = "Normal"
        }

        viewHolder.mPriorityView?.text = "Ringer Preference:  ".plus(ringer)


        return newView

    }

    internal class ViewHolder {
        var position: Int = 0
        var mItemLayout: RelativeLayout? = null
        var mTitleView: TextView? = null
        var mPlaceNameView: TextView? = null
        var mPriorityView: TextView? = null

    }


}