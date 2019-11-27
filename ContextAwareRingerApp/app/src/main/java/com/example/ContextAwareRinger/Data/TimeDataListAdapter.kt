package com.example.ContextAwareRinger.Data

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class TimeDataListAdapter (private val mContext: Context) : BaseAdapter(){
    private val mItems = ArrayList<TimeData>()

    fun add(item: TimeData){
        mItems.add(item)
        notifyDataSetChanged()
    }

    fun clear(){
        mItems.clear()
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

        return convertView!!
    }
}