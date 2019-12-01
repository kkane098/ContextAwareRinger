package com.example.ContextAwareRinger.Activities;

import android.widget.RadioButton
import android.content.pm.PackageManager
import android.os.Build
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.*
import com.google.android.gms.awareness.fence.DetectedActivityFence


class ActivitiesFragment(private val volumeMap: MutableMap<String, Int>) : Fragment(){

    private val TAG = "ActivitiesActivity"
    private val ACTIVITY_PERMISSION_REQUEST = 1

    lateinit var mActivitiesDataList: MutableList<ActivityData>
    private var currDataIdx : Int? = null
    private var currRadioGroup : RadioGroup? = null

    private fun needsActivityRuntimePermission(): Boolean {
        // Runtime permission requirement added in Android 10 (API level 29)
        return Build.VERSION.SDK_INT >= 29 && checkSelfPermission(context!!,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == ACTIVITY_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                updateActivityFences(currDataIdx!!, currRadioGroup!!.checkedRadioButtonId)
            } else {
                Toast.makeText(
                    context!!,
                    "You need to grant activity detection permissions for this app!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                currRadioGroup!!.clearCheck()
            }
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.activity, container, false)

        mActivitiesDataList = readActivityDataList(context!!, ACTIVITY_LIST_FILENAME).toMutableList()

        if(mActivitiesDataList.size == 0){
            val activityFoot = ActivityData(ACTIVITY_FOOT, ACTIVITY_FOOT_KEY, -1)
            val activityVehicle = ActivityData(ACTIVITY_VEHICLE, ACTIVITY_VEHICLE_KEY, -1)
            val activityBicycle = ActivityData(ACTIVITY_BICYCLE, ACTIVITY_BICYCLE_KEY, -1)

            mActivitiesDataList.add(0,activityFoot)
            mActivitiesDataList.add(1, activityVehicle)
            mActivitiesDataList.add(2, activityBicycle)
        }

        when(mActivitiesDataList[0].ringerMode){
            AudioManager.RINGER_MODE_SILENT -> {
                val silentButton = rootView.findViewById<RadioButton>(R.id.radioButtonOnFoot1)
                silentButton.isChecked = true
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateButton = rootView.findViewById<RadioButton>(R.id.radioButtonOnFoot2)
                vibrateButton.isChecked = true
            }
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalButton = rootView.findViewById<RadioButton>(R.id.radioButtonOnFoot3)
                normalButton.isChecked = true
            }
        }

        when(mActivitiesDataList[1].ringerMode){
            AudioManager.RINGER_MODE_SILENT -> {
                val silentButton = rootView.findViewById<RadioButton>(R.id.radioButtonVehicle1)
                silentButton.isChecked = true
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateButton = rootView.findViewById<RadioButton>(R.id.radioButtonVehicle2)
                vibrateButton.isChecked = true
            }
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalButton = rootView.findViewById<RadioButton>(R.id.radioButtonVehicle3)
                normalButton.isChecked = true
            }
        }

        when(mActivitiesDataList[2].ringerMode){
            AudioManager.RINGER_MODE_SILENT -> {
                val silentButton = rootView.findViewById<RadioButton>(R.id.radioButtonBicycle1)
                silentButton.isChecked = true
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateButton = rootView.findViewById<RadioButton>(R.id.radioButtonBicycle2)
                vibrateButton.isChecked = true
            }
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalButton = rootView.findViewById<RadioButton>(R.id.radioButtonBicycle3)
                normalButton.isChecked = true
            }
        }

        var footGroup: RadioGroup = rootView.findViewById(R.id.activity_radio_on_foot) as RadioGroup
        var vehicleGroup: RadioGroup = rootView.findViewById(R.id.activity_vehicle_radio) as RadioGroup
        var bicycleGroup: RadioGroup = rootView.findViewById(R.id.activity_bicycle_radio) as RadioGroup

        footGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val button = rootView.findViewById<RadioButton>(checkedId)
            if(button != null && button.isChecked) {
                currRadioGroup = radioGroup
                currDataIdx = ACTIVITY_FOOT
                updateActivityFences(ACTIVITY_FOOT, checkedId)
            }
        }

        vehicleGroup.setOnCheckedChangeListener { radioGroup , checkedId ->
            val button = rootView.findViewById<RadioButton>(checkedId)
            if(button != null && button.isChecked) {
                currRadioGroup = radioGroup
                currDataIdx = ACTIVITY_VEHICLE
                updateActivityFences(ACTIVITY_VEHICLE, checkedId)
            }
        }

        bicycleGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            val button = rootView.findViewById<RadioButton>(checkedId)
            if(button != null && button.isChecked) {
                currRadioGroup = radioGroup
                currDataIdx = ACTIVITY_BICYCLE
                updateActivityFences(ACTIVITY_BICYCLE, checkedId)
            }
        }

        return rootView
    }

    private fun updateActivityFences(dataIdx: Int, checkedId: Int){
        var ringerMode = -1

        if((checkedId == R.id.radioButtonBicycle1) || (checkedId == R.id.radioButtonOnFoot1) || (checkedId == R.id.radioButtonVehicle1)){
            ringerMode = AudioManager.RINGER_MODE_SILENT
            Log.i(TAG, "Changed to Silent")
        }
        else if((checkedId == R.id.radioButtonBicycle2) || (checkedId == R.id.radioButtonOnFoot2) || (checkedId == R.id.radioButtonVehicle2)){
            ringerMode = AudioManager.RINGER_MODE_VIBRATE
            Log.i(TAG, "Changed to Vibrate")
        }
        else if((checkedId == R.id.radioButtonBicycle3) || (checkedId == R.id.radioButtonOnFoot3) || (checkedId == R.id.radioButtonVehicle3)){
            ringerMode = AudioManager.RINGER_MODE_NORMAL
            Log.i(TAG, "Changed to Normal")
        }

        if(needsActivityRuntimePermission()){
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_PERMISSION_REQUEST
            )
        }
        else {
            var currData = mActivitiesDataList[dataIdx]
            mActivitiesDataList[dataIdx] =
                ActivityData(currData.activityType, currData.fenceKey, ringerMode)

            writeActivityDataList(context!!, mActivitiesDataList, ACTIVITY_LIST_FILENAME)
            volumeMap[currData.fenceKey] = ringerMode
            writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)

            // Register the Fence
            if (currData.activityType == ACTIVITY_FOOT) {
                registerDetectedActivityFence(
                    context!!,
                    DetectedActivityFence.ON_FOOT,
                    currData.fenceKey
                )
            } else if (currData.activityType == ACTIVITY_VEHICLE) {
                registerDetectedActivityFence(
                    context!!,
                    DetectedActivityFence.IN_VEHICLE,
                    currData.fenceKey
                )
            } else if (currData.activityType == ACTIVITY_BICYCLE) {
                registerDetectedActivityFence(
                    context!!,
                    DetectedActivityFence.ON_BICYCLE,
                    currData.fenceKey
                )
            }
        }
    }
}
