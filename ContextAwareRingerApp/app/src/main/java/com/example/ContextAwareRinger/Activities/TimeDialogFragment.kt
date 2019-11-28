package com.example.ContextAwareRinger.Activities

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ContextAwareRinger.R
import com.example.ContextAwareRinger.TimeViewModel
import java.util.*


class TimeDialogFragment: DialogFragment () {
    val TAG = "Time Dialog Fragment"
    private lateinit var viewModel: TimeViewModel

    private var hour : Int? = -1
    private var minute : Int? = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.time_dialog, container, false)
        val timeButton : Button = rootView.findViewById(R.id.startTimeButton)
        val submitButton : Button = rootView.findViewById(R.id.buttonSubmitTime)

        //Initialize viewmodel and observe the time data
        viewModel = ViewModelProviders.of(activity!!).get(TimeViewModel::class.java)

        //Update hour when it is changed by the timePicker Fragment
        val hourObserver = Observer { hour: Int? ->
            Log.i(TAG, "Hour updated")
            this.hour = hour

        }

        //Update minute and the textview when minute is changed by the timePicker Fragment
        val minuteObserver = Observer { min: Int? ->
            Log.i(TAG, "Hour updated")
            minute = min
            val timeText : TextView = rootView.findViewById(R.id.startTimeButton)
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


                timeText.text = "Time Selected - " + hourStr + ":" + minuteStr
            }

        }

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
        submitButton.setOnClickListener {
//            val
        }
        return rootView
    }

}

// Time Picking Fragment
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    val TAG = "Time Picker Fragment"
    private lateinit var viewModel: TimeViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel = ViewModelProviders.of(activity!!).get(TimeViewModel::class.java)
        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hourOfDay, minute,false)
    }

    // Called by TimePickerDialog when user sets the time
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        Log.i(TAG, "Updating times values in view model")
        viewModel.hour.value = hourOfDay
        viewModel.minute.value = minute
    }
}