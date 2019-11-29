package com.example.ContextAwareRinger.Activities;

import android.app.Activity;
import android.media.AudioManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.HEADPHONES_IN
import com.example.ContextAwareRinger.Data.HEADPHONES_OUT
import com.example.ContextAwareRinger.Data.HeadphonesData

//TODO: look into making this class a fragment
class HeadphonesActivity(private val volumeMap : MutableMap<String, Int>) : Fragment() {

    private val TAG = "HeadphonesActivity"
    lateinit var mHeadphonesDataList: MutableList<HeadphonesData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.headphones, container, false)

        mHeadphonesDataList = readHeadphonesDataList(context!!, HEADPHONES_LIST_FILENAME).toMutableList()

        if(mHeadphonesDataList.size == 0){
            val headPhonesIn = HeadphonesData(HEADPHONES_IN, "headphonesIn", -1)
            val headPhonesOut = HeadphonesData(HEADPHONES_OUT, "headphonesOut", -1)
            mHeadphonesDataList.add(headPhonesIn)
            mHeadphonesDataList.add(headPhonesOut)
        }
        // TODO: select appropriate radio button for headphones in
        when(mHeadphonesDataList[0].ringerMode){
            // TODO: repeat for each state, leave unselected if -1
            AudioManager.RINGER_MODE_NORMAL -> {
            }
        }
        // TODO: select appropriate radio button for headphones out
        when(mHeadphonesDataList[1].ringerMode){
            // TODO: repeat for each state, leave unselected if -1
            AudioManager.RINGER_MODE_NORMAL -> {
            }
        }

        var radioGroup: RadioGroup = rootView.findViewById(R.id.headphonesRadio) as RadioGroup

        // TODO: repeat for headphones out
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            //var selected = rootView.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            updateHeadphoneFence(0, checkedId)
        }


        return rootView
    }

    private fun updateHeadphoneFence(dataIdx: Int, checkId: Int){
        var ringerMode = -1

        if(checkId == R.id.radioButtonHeadphone1){
            ringerMode = AudioManager.RINGER_MODE_SILENT
            Log.i(TAG, "Do Not Disturb Selected " + ringerMode)
        }
        else if(checkId == R.id.radioButtonHeadphone2){
            ringerMode = AudioManager.RINGER_MODE_VIBRATE
            Log.i(TAG, "Vibrate Selected " + ringerMode)
        }
        else if(checkId == R.id.radioButtonHeadphone3){
            ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.i(TAG, "Normal Selected " + ringerMode)
        }

        when(dataIdx) {
            0 -> {
                mHeadphonesDataList[0] = HeadphonesData(HEADPHONES_IN, "headphonesIn", ringerMode)
                writeHeadphoneDataList(context!!, mHeadphonesDataList, HEADPHONES_LIST_FILENAME)
                volumeMap["headphonesIn"] = ringerMode
                writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)
                registerHeadphoneInFence(context!!, "headphonesIn")
            }
            1 -> {
                mHeadphonesDataList[1] = HeadphonesData(HEADPHONES_OUT, "headphonesOut", ringerMode)
                writeHeadphoneDataList(context!!, mHeadphonesDataList, HEADPHONES_LIST_FILENAME)
                volumeMap["headphonesOut"] = ringerMode
                writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)
                registerHeadphoneInFence(context!!, "headphonesOut")
            }
        }

    }
}
