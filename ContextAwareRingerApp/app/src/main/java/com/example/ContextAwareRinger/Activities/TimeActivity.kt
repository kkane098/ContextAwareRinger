package com.example.ContextAwareRinger.Activities;

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeActivity : Fragment() {
    val TAG = "Time Activity"
    var floatingActionButton : FloatingActionButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater!!.inflate(R.layout.time, container, false)
        floatingActionButton = rootView.findViewById(R.id.timeFAB)

        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            Log.i(TAG, "Adding onclick listener to fab")
            val ft = fragmentManager!!.beginTransaction()
            val dialogFragment = TimeDialogFragment()
            dialogFragment.show(ft, "dialog")
        }

        return rootView
    }

    private fun showAddDialog() {

        Log.i(TAG, "Showing time dialog")

//        //Make the Dialog visible
//        val dialogBuilder = AlertDialog.Builder(activity as Context)
//        val inflater = layoutInflater
//        val dialogView = TimeDialogFragment.kt()
//        dialogBuilder.setView(dialogView)
//        dialogBuilder.setTitle("Add Time")
//        val b = dialogBuilder.create()
//        b.show()
//
//        //Get the UI components that contain information used to create a LocationData object
//        val buttonUpdate = dialogView.findViewById<Button>(R.id.buttonUpdateAuthor)
//        val locationTitle : EditText? = dialogView.findViewById(R.id.locationTitle)
//        val radiusEditText : EditText? = dialogView.findViewById(R.id.radiusEditText)
//        val radioGroup : RadioGroup = dialogView.findViewById(R.id.volumeRadioGroup)

        //Set the onClick method for the submit button. It should not submit unless there are values for all a
//        buttonUpdate.setOnClickListener {
//        }

    }
}
