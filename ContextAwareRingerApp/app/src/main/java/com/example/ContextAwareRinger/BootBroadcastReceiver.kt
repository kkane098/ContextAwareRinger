package com.example.ContextAwareRinger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.ContextAwareRinger.Data.HEADPHONES_IN

class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val locationDataList = readLocationDataList(context, LOCATION_LIST_FILENAME)
        val headphonesDataList = readHeadphonesDataList(context, HEADPHONES_LIST_FILENAME)
        val activityDataList = readActivityDataList(context, ACTIVITY_LIST_FILENAME)

        Log.i(TAG, "Boot Receiver is running")

        for(locationData in locationDataList){
            registerLocationFence(context, locationData.lat, locationData.lng, locationData.radius, locationData.fenceKey)
        }

        for(headPhonesData in headphonesDataList){
            if(headPhonesData.headphoneState == HEADPHONES_IN){
                registerHeadphoneInFence(context, headPhonesData.fenceKey)
            }
            else {
                registerHeadphoneOutFence(context, headPhonesData.fenceKey)
            }
        }

        for(activityData in activityDataList){
            registerDetectedActivityFence(context, activityData.activityType, activityData.fenceKey)
        }
    }

    companion object {
        val TAG = "BOOT_RECEIVER"
    }
}
