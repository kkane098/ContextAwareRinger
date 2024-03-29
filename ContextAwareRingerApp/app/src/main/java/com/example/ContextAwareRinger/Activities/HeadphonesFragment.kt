package com.example.ContextAwareRinger.Activities

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

class HeadphonesFragment(private val volumeMap : MutableMap<String, Int>) : Fragment() {

    private val TAG = "HeadphonesActivity"
    lateinit var mHeadphonesDataList: MutableList<HeadphonesData>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.headphones, container, false)

        mHeadphonesDataList = readHeadphonesDataList(context!!, HEADPHONES_LIST_FILENAME).toMutableList()
        Log.i(TAG, "$mHeadphonesDataList")

        if(mHeadphonesDataList.size == 0){
            val headPhonesIn = HeadphonesData(HEADPHONES_IN, "headphonesIn", -1)
            val headPhonesOut = HeadphonesData(HEADPHONES_OUT, "headphonesOut", -1)
            mHeadphonesDataList.add(headPhonesIn)
            mHeadphonesDataList.add(headPhonesOut)
        }
        when(mHeadphonesDataList[0].ringerMode){
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone3)
                normalRadioButton.isChecked = true
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone2)
                vibrateRadioButton.isChecked = true
            }
            AudioManager.RINGER_MODE_SILENT -> {
                val silentRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone1)
                silentRadioButton.isChecked = true
            }
        }
        when(mHeadphonesDataList[1].ringerMode){
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone6)
                normalRadioButton.isChecked = true
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone5)
                vibrateRadioButton.isChecked = true
            }
            AudioManager.RINGER_MODE_SILENT -> {
                val silentRadioButton = rootView.findViewById<RadioButton>(R.id.radioButtonHeadphone4)
                silentRadioButton.isChecked = true
            }
        }

        var inRadioGroup: RadioGroup = rootView.findViewById(R.id.headphonesRadio) as RadioGroup
        var outRadioGroup: RadioGroup = rootView.findViewById(R.id.headphonesRadio2) as RadioGroup

        inRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            //var selected = rootView.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            updateHeadphoneFence(0, checkedId)
        }

        outRadioGroup.setOnCheckedChangeListener {_, checkedId ->
            Log.i(TAG, "changed out")
            updateHeadphoneFence(1, checkedId)
        }

        return rootView
    }

    private fun updateHeadphoneFence(dataIdx: Int, checkId: Int){
        var ringerMode = -1

        if(checkId == R.id.radioButtonHeadphone1 || checkId == R.id.radioButtonHeadphone4){
            ringerMode = AudioManager.RINGER_MODE_SILENT
            Log.i(TAG, "Do Not Disturb Selected " + ringerMode)
        }
        else if(checkId == R.id.radioButtonHeadphone2 || checkId == R.id.radioButtonHeadphone5){
            ringerMode = AudioManager.RINGER_MODE_VIBRATE
            Log.i(TAG, "Vibrate Selected " + ringerMode)
        }
        else if(checkId == R.id.radioButtonHeadphone3 || checkId == R.id.radioButtonHeadphone6){
            ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.i(TAG, "Normal Selected " + ringerMode)
        }

        val currData = mHeadphonesDataList[dataIdx]
        mHeadphonesDataList[dataIdx] = HeadphonesData(currData.headphoneState, currData.fenceKey, ringerMode)
        writeHeadphoneDataList(context!!, mHeadphonesDataList, HEADPHONES_LIST_FILENAME)
        volumeMap[currData.fenceKey] = ringerMode
        writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)
        if(currData.headphoneState == HEADPHONES_IN) {
            registerHeadphoneInFence(context!!, currData.fenceKey)
        }
        else {
            registerHeadphoneOutFence(context!!, currData.fenceKey)
        }
    }
}
