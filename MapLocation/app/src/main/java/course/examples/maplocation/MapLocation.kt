package course.examples.maplocation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


import java.util.*


// Several Activity lifecycle methods are instrumented to emit LogCat output
// so you can follow this class' lifecycle

class MapLocation : Activity() {

    companion object {
        const val TAG = "MapLocation"
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    // UI elements
    private lateinit var addrText: EditText
    private lateinit var button: Button
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {

        /*
        Required call through to Activity.onCreate()
        Restore any saved instance state, if necessary
        */
        super.onCreate(savedInstanceState)

        // Set content view
        setContentView(R.layout.main)

        // Initialize UI elements
        addrText = findViewById(R.id.location)
        button = findViewById(R.id.mapButton)

        // Link UI elements to actions in code
        button.setOnClickListener { processClick() }

      //  Places.initialize(this@MapLocation,"AIzaSyCHdq980kiy104lTBlpPJNAcZjzRHj3IcI")


    }

    // Called when user clicks the Show Map button

    private fun processClick() {


            // Process text for network transmission

        if(!Places.isInitialized()){
            Places.initialize(this@MapLocation,"AIzaSyCHdq980kiy104lTBlpPJNAcZjzRHj3IcI")
            //Places.initialize(this@MapLocation,"AIzaSyAWn7tc3sUCiSXHFyKqUvJs-ooZfRAnSO4") // Restricted key
            // The restricted key only works on android devices with course.examples.maplocation package
            // on my computer.
        }

        var fields = Arrays.asList(Place.Field.ID,Place.Field.NAME, Place.Field.ADDRESS)
        var i = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN,fields).setCountry("US").build(this)

        startActivityForResult(i, AUTOCOMPLETE_REQUEST_CODE)



            // Create Intent object for starting Google Maps application
            /*val geoIntent = Intent(
                    Intent.ACTION_VIEW, Uri
                    .parse("geo:0,0?q=$address"))

            if (packageManager.resolveActivity(geoIntent, 0) != null) {
                // Use the Intent to start Google Maps application using Activity.startActivity()
                startActivity(geoIntent)
            }*/

            }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                var p = Autocomplete.getPlaceFromIntent(data!!)
                //addrText?.setText(p.name + " " + p.address + " "+ p.id)
                addrText?.setText(p.address)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "The activity is visible and about to be started.")
    }


    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "The activity is visible and about to be restarted.")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "The activity is visible and has focus (it is now \"resumed\")")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG,
                "Another activity is taking focus (this activity is about to be \"paused\")")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "The activity is no longer visible (it is now \"stopped\")")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "The activity is about to be destroyed.")
    }
}
