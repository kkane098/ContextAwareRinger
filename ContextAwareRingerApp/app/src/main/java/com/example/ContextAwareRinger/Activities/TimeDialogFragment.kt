package com.example.ContextAwareRinger.Activities

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R
import java.util.*






class TimeDialogFragment: DialogFragment () {
    val TAG = "Time Dialog Fragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.time_dialog, container, false)
        val timeButton : Button = rootView.findViewById(R.id.startTimeButton)

        timeButton.setOnClickListener {
            val ft = fragmentManager!!.beginTransaction()
            val prev = fragmentManager!!.findFragmentByTag("dialog")

            if (prev != null) {
                ft.remove(prev)
            }

            ft.addToBackStack(null)
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(ft, "dialog")
//            ft.commit()

////            fragmentManager!!.beginTransaction()
////                .addToBackStack(null)
////                .commit()
//            ft.commit()

        }

        return rootView
    }
    fun onActivityResult() {

    }
    fun timeSelected(hour:Int, minute: Int) {
        Log.i(TAG, "Selected hour: " + hour + " Minute: " + minute)
    }
}
// Time Picking Fragment
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    val TAG = "Time Picker Fragment"
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hourOfDay, minute,false)
    }

    // Callback called by TimePickerDialog when user sets the time
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val list = fragmentManager!!.fragments
        list.iterator().forEachRemaining {
            Log.i(TAG, it::class.java.toString())


//        val prevFrag = list.get(list.size - 2) as TimeDialogFragment
//        if (prevFrag != null) {
//            prevFrag.timeSelected(hourOfDay, minute)
//            fragmentManager!!.popBackStack()
        }
    }
}