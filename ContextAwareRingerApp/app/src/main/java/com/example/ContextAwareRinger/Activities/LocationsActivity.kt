package com.example.ContextAwareRinger.Activities;

import androidx.lifecycle.Observer
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
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
import com.example.ContextAwareRinger.Data.LocationDataListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.text.FieldPosition
import java.util.*

class LocationsActivity(private val volumeMap: MutableMap<String, Int>) : Fragment() {

    var TAG = "LocationActivity"
    var floatingActionButton: FloatingActionButton? = null
    val LOCATION_PERMISSION_REQUEST = 1
    val AUTOCOMPLETE_REQUEST_CODE = 2

    var mLat: Double? = null
    var mLong: Double? = null
    var mPlaceName: String? = null
    lateinit var mLocationList: MutableList<LocationData>
    internal lateinit var  locationListView: ListView
    internal lateinit var locationAdapter: LocationDataListAdapter
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

        locationListView = rootView.findViewById(R.id.ListViewLocation) as ListView
        locationAdapter = LocationDataListAdapter(context!!)

        locationListView.adapter = locationAdapter

        locationListView?.setOnItemLongClickListener { parent, view, position, id ->
            val location = locationAdapter.getItem(position) as LocationData
            showUpdateDeleteDialog(location, position)
            true
        }

        mLocationList = readLocationDataList(context!!, LOCATION_LIST_FILENAME).toMutableList()
        Log.i(TAG, "list was $mLocationList")

        Log.i(TAG, "Adding onclick listener to fab")
        //Set the onclick for the floating button to open a dialog box
        floatingActionButton?.setOnClickListener {
            showAddDialog()
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()

    }

    // Need to load the listViewAdapter
    override fun onResume() {
        super.onResume()
        if(locationAdapter.count == 0) {
            val locList = readLocationDataList(context!!, LOCATION_LIST_FILENAME)

            for (i in locList) {
                locationAdapter.add(i)
            }
        }
    }

    private fun showUpdateDeleteDialog(location: LocationData, position: Int){
        Log.i(TAG,"Update or Delete location")
        //TODO: Set the Update-Delete Dialog
        mLat = location.lat
        mLong = location.lng
        mPlaceName = location.placeName

        val dialogBuilder = AlertDialog.Builder(activity as Context)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_delete_location_dialog, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setTitle("Update or Delete Location")
        val b = dialogBuilder.create()
        b.show()

        //Get the UI components that contain information used to create a LocationData object
        val buttonUpdate = dialogView.findViewById<Button>(R.id.buttonUpdate)
        val buttonDelete = dialogView.findViewById<Button>(R.id.buttonDelete)
        val locationTitle: EditText? = dialogView.findViewById(R.id.locationTitleLocationUpdateDelete)
        val radiusEditText: EditText? = dialogView.findViewById(R.id.radiusEditTextLocationUpdateDelete)
        val radioGroup: RadioGroup = dialogView.findViewById(R.id.volumeRadioGroupLocationUpdateDelete)
        val buttonLocation: Button = dialogView.findViewById(R.id.place_autocomplete_buttonLocationUpdateDelete)

        locationTitle!!.setText(location.name)
        radiusEditText!!.setText(location.radius.toInt().toString())

        when(location.ringerMode){
            AudioManager.RINGER_MODE_VIBRATE -> {
                val vibrateButton = dialogView.findViewById<RadioButton>(R.id.radioButton2)
                vibrateButton.isChecked = true
            }
            AudioManager.RINGER_MODE_SILENT -> {
                val silentButton = dialogView.findViewById<RadioButton>(R.id.radioButton)
                silentButton.isChecked = true
            }
            AudioManager.RINGER_MODE_NORMAL -> {
                val normalButton = dialogView.findViewById<RadioButton>(R.id.radioButton3)
                normalButton.isChecked = true
            }
        }

        buttonLocation.setOnClickListener {
            processClick()
        }

        //TODO: Handle Update
        buttonUpdate.setOnClickListener {
            val title = locationTitle?.text.toString().trim { it <= ' ' }

            var radius = location.radius
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

                    //TODO: Store location data in the file system
                    Log.i(TAG, "submitting")
                    b.dismiss()
                    //TODO: Update the location
                    deleteLocation(location)
                    updateLocation(title, mPlaceName!!, radius, mLat!!, mLong!!, location.fenceKey, ringerMode, position)
                }
            } else {
                Toast.makeText(
                    context!!,
                    "Please enter all requested information",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //TODO: Handle Delete
        buttonDelete.setOnClickListener{
            deleteLocation(location)
            //TODO: close the dialog box
            b.dismiss()
        }
    }

    private fun showAddDialog() {

        Log.i(TAG, "Adding location")

        //Reset mLat, mLong, and mPlaceName
        mLat = null
        mLong = null
        mPlaceName = null

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
            if (!TextUtils.isEmpty(title) && !radiusEditText?.text.isNullOrEmpty() && radioGroup.checkedRadioButtonId != -1 && mLat != null && mLong != null && mPlaceName != null) {
                if (needsLocationRuntimePermission()) {
                    Log.i(TAG, "requesting permissions")
                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ), LOCATION_PERMISSION_REQUEST
                    )
                } else {
                    Log.i(TAG, "submitting")
                    b.dismiss()
                    createLocation(title, mPlaceName!!, radius, mLat!!, mLong!!, fenceKey, ringerMode)
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

                mLat = p.latLng?.latitude
                mLong = p.latLng?.longitude
                mPlaceName = p.name

                Toast.makeText(context!!, "You chose $mPlaceName", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createLocation(
        title: String,
        placeName: String,
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
        val data = LocationData(title, placeName, latitude, longitude, radius, fenceKey, ringerMode)

        mLocationList.add(data)
        writeLocationDataList(context!!, mLocationList, LOCATION_LIST_FILENAME)

        // Doing adapter stuff here
        locationAdapter.add(data)

        volumeMap[fenceKey] = ringerMode
        writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)

        // TODO: Register the fence
        registerLocationFence(context!!,latitude,longitude,radius,fenceKey)

    }

    private fun updateLocation(
        title: String,
        placeName: String,
        radius: Double,
        latitude: Double,
        longitude: Double,
        fenceKey: String,
        ringerMode: Int,
        i: Int
    ) {
        Log.i(
            TAG,
            "Location created with data: \n  Title: $title Radius: $radius Latitude: $latitude Longitude: $longitude Fence key: $fenceKey ringerMode: $ringerMode"
        )
        val data = LocationData(title, placeName, latitude, longitude, radius, fenceKey, ringerMode)

        mLocationList.add(data)
        writeLocationDataList(context!!, mLocationList, LOCATION_LIST_FILENAME)

        // Doing adapter stuff here
        locationAdapter.add(data,i)

        volumeMap[fenceKey] = ringerMode
        writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)

        // TODO: Register the fence
        registerLocationFence(context!!,latitude,longitude,radius,fenceKey)
    }

    private fun deleteLocation(item: LocationData){
        Log.i(
            TAG,
            "Location Deleted with data: \n  Title: ${item.name} Radius: ${item.radius} Latitude: ${item.lat} Longitude: ${item.lng} Fence key: ${item.fenceKey} ringerMode: ${item.ringerMode}"
        )
        // Remove old location
        mLocationList.remove(item)
        // Add new location
        writeLocationDataList(context!!, mLocationList, LOCATION_LIST_FILENAME)


        volumeMap.remove(item.fenceKey)
        writeVolumeMap(context!!, volumeMap, VOLUME_MAP_FILENAME)

        //TODO: Unregister the Fence
        unregisterFence(context!!,item.fenceKey)

        // Doing adapter stuff here
        locationAdapter.delete(item)

    }
}

