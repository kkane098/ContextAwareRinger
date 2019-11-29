package com.example.ContextAwareRinger.Activities


import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ContextAwareRinger.Data.LocationData
import com.example.ContextAwareRinger.Data.TimeData
import java.util.*
import android.media.AudioManager
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.TimeDataListAdapter


class TimeUpdateDeleteDialogFragment(var timeDataList : MutableList<TimeData>, var volumeMap: MutableMap<String, Int>,
                         var timeAdapter: TimeDataListAdapter, var timeItem: TimeData, var positionInView: Int): DialogFragment () {
    val TAG = "Time Dialog Fragment"
    private lateinit var viewModel: TimeViewModel

    private var hour : Int? = timeItem.hour
    private var minute : Int? = timeItem.min

    private lateinit var timeButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.update_delete_time_dialog, container, false)

        val updateButton : Button = rootView.findViewById(R.id.buttonUpdateTime)
        val deleteButton: Button = rootView.findViewById(R.id.buttonDeleteTime)
        val radioGroup : RadioGroup = rootView.findViewById(R.id.volumeRadioGroupTime)
        val spinner : Spinner = rootView.findViewById(R.id.timeSelectionSpinner)
        timeButton = rootView.findViewById(R.id.startTimeButton)
        //Initialize viewmodel and observe the time data
        viewModel = ViewModelProviders.of(activity!!).get(TimeViewModel::class.java)
        Log.i(TAG, "Oncreate called")

        //TODO: Add code so that information is restored when update dialog box is started
        var tempHr = (timeItem.hour%12).toString()
        if(timeItem.hour%12 == 0){
            tempHr = "12"
        }
        var tempMn = ""
        if(timeItem.min > 9){
            tempMn = timeItem.min.toString()
        }
        else{
            tempMn = "0" + timeItem.min
        }
        timeButton.text = "Time Selected - " + tempHr + ":" + tempMn

        /*this.hour = timeItem.hour!!
        this.minute = timeItem.min!!
        Log.i(TAG,"CHECKING IF HOUR WORKING" + this.hour.toString())
        Log.i(TAG,"CHECKING IF MINUTE WORKING" + this.minute.toString())*/

        when(timeItem.ringerMode){
            AudioManager.RINGER_MODE_SILENT -> (rootView.findViewById(R.id.radioButtonUpdateDeleteTime) as RadioButton).isChecked = true
            AudioManager.RINGER_MODE_VIBRATE -> (rootView.findViewById(R.id.radioButtonUpdateDeleteTime2) as RadioButton).isChecked = true
            AudioManager.RINGER_MODE_NORMAL -> (rootView.findViewById(R.id.radioButtonUpdateDeleteTime3) as RadioButton).isChecked = true

        }

        when(timeItem.day){
            DAILY -> spinner.setSelection(0)
            WEEKEND -> spinner.setSelection(1)
            WEEKDAY -> spinner.setSelection(2)
            Calendar.MONDAY -> spinner.setSelection(3)
            Calendar.TUESDAY -> spinner.setSelection(4)
            Calendar.WEDNESDAY -> spinner.setSelection(5)
            Calendar.THURSDAY -> spinner.setSelection(6)
            Calendar.FRIDAY -> spinner.setSelection(7)
            Calendar.SATURDAY -> spinner.setSelection(8)
            Calendar.SUNDAY -> spinner.setSelection(9)
        }

        // -------------------------------------------------------------------------------

        //Update hour when it is changed by the timePicker Fragment
        val hourObserver = Observer { hour: Int? ->
            Log.i(TAG, "Hour updated")
            this.hour = hour

        }

        //Update minute and the textview when minute is changed by the timePicker Fragment
        val minuteObserver = Observer { min: Int? ->
            Log.i(TAG, "Hour updated")
            //Store the value in this fragment to be used later
            minute = min
            //Get a reference to the time select button to update the text
            val timeText : TextView = rootView.findViewById(R.id.startTimeButton)

            //Check that appropriate values have been provided
            if (hour != null && minute != null && hour != -1 && minute != 1) {
                var hourStr = ""

                //Pad hour with a 0 if it is < 10
                if (hour.toString().length > 1) {
                    hourStr = hour.toString()
                } else {
                    hourStr = "0" +hour.toString()
                }

                var minuteStr = ""

                //Pad minute with a 0 if it is <10
                if (minute.toString().length > 1) {
                    minuteStr = minute.toString()
                } else {
                    minuteStr = "0" + minute.toString()
                }

                //Update the button text to indicate the user's selection
                timeText.text = "Time Selected - " + hourStr + ":" + minuteStr
            }

        }

        //Attach observers to the viewmodel
        viewModel.hour.observe(this, hourObserver)
        viewModel.minute.observe(this, minuteObserver)

        //Set onClick for the select time button
        timeButton.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")

            if (prev != null) {
                ft.remove(prev)
            }

            //Return to this fragment after the timePicker finishes
            ft.addToBackStack(null)

            //Create a timePicker Fragment
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(ft, "dialog")
        }

        //Set onclick for the submit button
        updateButton.setOnClickListener {
            //Get selected volume
            var selected : RadioButton? = null

            if (radioGroup.checkedRadioButtonId != -1) {
                selected = rootView.findViewById(radioGroup.checkedRadioButtonId)
            }

            var ringerMode : Int? = null
            //Convert radio button option to an int
            if (selected != null) {
                if (selected.text == "Do Not Disturb") {
                    ringerMode = AudioManager.RINGER_MODE_SILENT
                } else if (selected.text == "Vibrate") {
                    ringerMode = AudioManager.RINGER_MODE_VIBRATE
                } else {
                    ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            }

            //Get repetition from spinner
            var repetitionInterval : Int = getRepetitionInterval(spinner)

            val workKey = timeItem.workKey

            //Ensure that all user input is provided
            if ((radioGroup.checkedRadioButtonId != -1) && (hour != -1) && (minute != -1)) {
                //Close the dialog box
                dismiss()

                Log.i(TAG, "Time object created with Hour: " + hour + ", Minute: " + minute + ", Volume: " + selected!!.text + ", Interval: " + spinner.selectedItem.toString())

                //TODO: Delete the existing time object
                timeDataList.remove(timeItem)
                writeTimeDataList(context!!,timeDataList, TIME_LIST_FILENAME)

                volumeMap.remove(timeItem.workKey)
                writeVolumeMap(context!!,volumeMap, VOLUME_MAP_FILENAME)

                timeAdapter.delete(timeItem)

                //TODO: Unregister the Time Worker
                removeTimeWorker(context!!, timeItem.workKey)

                //TODO: Add the updated time object
                val time = TimeData(hour!!, minute!!, repetitionInterval, workKey, ringerMode!!)
                volumeMap[workKey] = ringerMode!!
                writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)
                timeDataList.add(positionInView,time)
                timeAdapter.add(time,positionInView)
                writeTimeDataList(context!!, timeDataList, TIME_LIST_FILENAME)

                //TODO: Register Time Worker
                enqueueTimeWorker(context!!, hour!!, minute!!, repetitionInterval, ringerMode!!, workKey)
            }
            else {
                Toast.makeText(
                    context!!,
                    "Please enter all requested information",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        deleteButton.setOnClickListener {
            //TODO: Delete the item
            timeDataList.remove(timeItem)
            writeTimeDataList(context!!,timeDataList, TIME_LIST_FILENAME)

            volumeMap.remove(timeItem.workKey)
            writeVolumeMap(context!!,volumeMap, VOLUME_MAP_FILENAME)

            timeAdapter.delete(timeItem)

            //TODO: Unregister the fence
            removeTimeWorker(context!!, timeItem.workKey)
            dismiss()
        }

        return rootView
    }

    /*override fun onDetach() {
        super.onDetach()
        viewModel.hour.value = -1
        viewModel.minute.value = -1
    }*/



}

