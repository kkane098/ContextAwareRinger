package com.example.ContextAwareRinger.Activities;

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle;
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.Data.LocationData
import com.example.ContextAwareRinger.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LocationsActivity : Fragment(){

    var TAG = "LocationActivity"
    var floatingActionButton : FloatingActionButton? = null
    val LOCATION_PERMISSION_REQUEST = 1

    private fun needsLocationRuntimePermission(): Boolean {
        // Prior to Android 10, only needed FINE_LOCATION, but now also need BACKGROUND_LOCATION
        return when {
            Build.VERSION.SDK_INT >= 29 -> checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(context!!, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            else -> false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.location, container, false)
        floatingActionButton = rootView.findViewById(R.id.locationFAB)

        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            Log.i(TAG, "Adding onclick listener to fab")
            showAddDialog()
        }

        return rootView
    }

    private fun showAddDialog() {

        Log.i(TAG, "Adding location")

        //Make the Dialog visible
        val dialogBuilder = AlertDialog.Builder(activity as Context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_location_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Add Location")
        val b = dialogBuilder.create()
        b.show()

        //Get the UI components that contain information used to create a LocationData object
        val buttonUpdate = dialogView.findViewById<Button>(R.id.buttonUpdateAuthor)
        val locationTitle : EditText? = dialogView.findViewById(R.id.locationTitle)
        val radiusEditText : EditText? = dialogView.findViewById(R.id.radiusEditText)
        val radioGroup : RadioGroup = dialogView.findViewById(R.id.volumeRadioGroup)

        //Set the onClick method for the submit button. It should not submit unless there are values for all a
        buttonUpdate.setOnClickListener {

            val title = locationTitle?.text.toString().trim { it <= ' ' }

            //Default radius to .5 miles
            var radius = .5
            //Update radius if user input exists
            if (!radiusEditText?.text.isNullOrEmpty()) {
                radius = java.lang.Double.parseDouble(radiusEditText?.text.toString())
            }

            //Extract the selected volume setting if it was selected
            var selected: RadioButton? = null
            var ringerMode = -1

            //Ensure that there is a radio button selected
            if (radioGroup.checkedRadioButtonId != -1) {
                selected = dialogView.findViewById(radioGroup.checkedRadioButtonId)
            }

            //Assign ringerMode based on which radio button is selected
            if (selected != null) {
                if (selected.text == "Do Not Disturb") {
                    ringerMode = AudioManager.RINGER_MODE_SILENT
                } else if (selected.text == "Vibrate") {
                    ringerMode = AudioManager.RINGER_MODE_VIBRATE
                } else {
                    ringerMode = AudioManager.RINGER_MODE_NORMAL
                }
            }

            //TODO: Add latitude and longitude to the ui, collect them into variables
            val latitude = 0.0
            val longitude = 0.0

            //TODO: Generate fence key
            val fenceKey = ""

            //If all the data fields are filled in, submit the data
            if (!TextUtils.isEmpty(title) && !radiusEditText?.text.isNullOrEmpty() && radioGroup.checkedRadioButtonId != -1) {
                if(needsLocationRuntimePermission()){
                    Log.i(TAG, "requesting permissions")
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ), LOCATION_PERMISSION_REQUEST
                    )
                }
                else {
                    //TODO: Add the new locationdata to the list view
                    //TODO: Store location data in the file system
                    Log.i(TAG, "submitting")
                    b.dismiss()
                    val data: LocationData =
                        createLocation(title, radius, latitude, longitude, fenceKey, ringerMode)
                }
            }
        }

    }

    private fun createLocation(title:String, radius:Double, latitude:Double, longitude:Double, fenceKey:String, ringerMode:Int) : LocationData {
        Log.i(TAG, "Location created with data: \n " + " Title: " + title + " Radius: " + radius +  " Latitude: " + latitude + " Longitude: " + longitude + " Fence key: " + fenceKey + " ringerMode: " + ringerMode)
        return LocationData(latitude, longitude, radius, fenceKey, ringerMode)
    }
}

