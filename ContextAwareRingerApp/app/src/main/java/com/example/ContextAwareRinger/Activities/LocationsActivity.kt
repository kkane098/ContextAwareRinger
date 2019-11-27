package com.example.ContextAwareRinger.Activities;

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.LocationData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class LocationsActivity(private val volumeMap: MutableMap<String, Int>) : Fragment() {

    var TAG = "LocationActivity"
    var floatingActionButton: FloatingActionButton? = null
    val LOCATION_PERMISSION_REQUEST = 1
    val AUTOCOMPLETE_REQUEST_CODE = 2

    var mLat: Double? = null
    var mLong: Double? = null
    lateinit var mLocationList: MutableList<LocationData>

    private fun needsLocationRuntimePermission(): Boolean {
        // Prior to Android 10, only needed FINE_LOCATION, but now also need BACKGROUND_LOCATION
        return when {
            Build.VERSION.SDK_INT >= 29 -> checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(
                        context!!,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> checkSelfPermission(
                context!!,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            else -> false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            // Checks if either both permissions are granted or FINE_LOCATION is granted and the device is running an Android version prior to 10
            if (!((grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) || (grantResults[0] == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < 29))) {
                Toast.makeText(
                    context!!,
                    "You need to grant ALL location permissions for this app!",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater!!.inflate(R.layout.location, container, false)
        floatingActionButton = rootView.findViewById(R.id.locationFAB)

        mLocationList = readLocationDataList(context!!, LOCATION_LIST_FILENAME).toMutableList()
        Log.i(TAG, "list was $mLocationList")

        Log.i(TAG, "Adding onclick listener to fab")
        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            showAddDialog()
        }

        return rootView
    }

    private fun showAddDialog() {

        Log.i(TAG, "Adding location")

        //Reset mLat and mLong
        mLat = null
        mLong = null

        //Make the Dialog visible
        val dialogBuilder = AlertDialog.Builder(activity as Context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.add_location_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Add Location")
        val b = dialogBuilder.create()
        b.show()

        //Get the UI components that contain information used to create a LocationData object
        val buttonSubmit = dialogView.findViewById<Button>(R.id.buttonSubmit)
        val locationTitle: EditText? = dialogView.findViewById(R.id.locationTitle)
        val radiusEditText: EditText? = dialogView.findViewById(R.id.radiusEditText)
        val radioGroup: RadioGroup = dialogView.findViewById(R.id.volumeRadioGroup)
        val buttonLocation: Button = dialogView.findViewById(R.id.place_autocomplete_button)

        buttonLocation.setOnClickListener {
            processClick()
        }
        //Set the onClick method for the submit button. It should not submit unless there are values for all a
        buttonSubmit.setOnClickListener {

            val title = locationTitle?.text.toString().trim { it <= ' ' }

            //Default radius to .5 miles
            var radius = 200.0
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

            val fenceKey = UUID.randomUUID().toString()

            //If all the data fields are filled in, submit the data
            if (!TextUtils.isEmpty(title) && !radiusEditText?.text.isNullOrEmpty() && radioGroup.checkedRadioButtonId != -1 && mLat != null && mLong != null) {
                if (needsLocationRuntimePermission()) {
                    Log.i(TAG, "requesting permissions")
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ), LOCATION_PERMISSION_REQUEST
                    )
                } else {
                    //TODO: Add the new locationdata to the list view
                    //TODO: Store location data in the file system
                    Log.i(TAG, "submitting")
                    b.dismiss()
                    createLocation(title, radius, mLat!!, mLong!!, fenceKey, ringerMode)
                }
            } else {
                Toast.makeText(
                    context!!,
                    "Please enter all requested information",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun processClick() {
        if (!Places.isInitialized()) {
            Places.initialize(context!!, "AIzaSyDOSplxleMLGCL0d6qfpkBwrt8x_vDGadY")
        }

        var fields =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        var i =
            Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).setCountry("US")
                .build(context!!)

        Log.i(TAG, "Starting place autocomplete")
        startActivityForResult(i, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            Log.i(TAG, "Activity finished")
            if (resultCode == RESULT_OK) {
                var p = Autocomplete.getPlaceFromIntent(data!!)
                //addrText?.setText(p.name + " " + p.address + " "+ p.id)
                //addrText?.setText(p.latLng.toString() + " Address: " + p.address)

                mLat = p.latLng?.latitude
                mLong = p.latLng?.longitude

                Toast.makeText(context!!, "You chose " + p.name, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createLocation(
        title: String,
        radius: Double,
        latitude: Double,
        longitude: Double,
        fenceKey: String,
        ringerMode: Int
    ) {
        Log.i(
            TAG,
            "Location created with data: \n  Title: $title Radius: $radius Latitude: $latitude Longitude: $longitude Fence key: $fenceKey ringerMode: $ringerMode"
        )
        val data = LocationData(title, latitude, longitude, radius, fenceKey, ringerMode)

        mLocationList.add(data)
        writeLocationDataList(context!!, mLocationList, LOCATION_LIST_FILENAME)

        volumeMap[fenceKey] = ringerMode
        writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)
    }
}

