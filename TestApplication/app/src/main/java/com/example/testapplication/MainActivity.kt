package com.example.testapplication

import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.os.Build
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.widget.Toast
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.*
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {

    private lateinit var mAudioManager: AudioManager
    private lateinit var mPendingIntent: PendingIntent
    private val LOCATION_FENCE_KEY = "TEST_FENCE_KEY"
    private val ACTIVITY_FENCE_KEY = "TEST_FENCE_KEY2"
    private val HEADPHONE_FENCE_KEY = "TEST_FENCE_KEY3"
    private val FENCE_RECEIVER_ACTION =
        "com.example.testapplication.FENCE_RECEIVER_ACTION"
    private val TAG = "TEST-CONTEXT"
    private val ACTIVITY_PERMISSION_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val intent = Intent(this@MainActivity, FenceBroadcastReceiver::class.java)
        intent.action = FENCE_RECEIVER_ACTION
        //intent.setPackage("com.example.testapplication")
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {

            val intent = Intent(
                android.provider.Settings
                    .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
            )

            startActivity(intent)
        }
    }


    private fun needsActivityRuntimePermission(): Boolean {
        // Runtime permission requirement added in Android 10 (API level 29)
        return Build.VERSION.SDK_INT >= 29 && checkSelfPermission(
            android.Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun needsLocationRuntimePermission(): Boolean {
        // Prior to Android 10, only needed FINE_LOCATION, but now also need BACKGROUND_LOCATION
        return when {
            Build.VERSION.SDK_INT >= 29 -> checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            else -> false
        }
    }

    private fun registerActivityFence() {
        //Toast.makeText(this, "registering fence", Toast.LENGTH_SHORT).show()
        val activityFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT)
        Awareness.getFenceClient(this).updateFences(
            FenceUpdateRequest.Builder().addFence(
                ACTIVITY_FENCE_KEY,
                activityFence,
                mPendingIntent
            ).build()
        ).addOnSuccessListener {
            Log.i(TAG, "Successfully registered fence")
        }.addOnFailureListener {
            Log.e(TAG, "Fence could not be registered: $it")
        }
    }

    private fun registerLocationFence() {
        val locationFence = LocationFence.`in`(39.001090, -76.942048, 200.0, 60 * 1000L)
        Awareness.getFenceClient(this).updateFences(
            FenceUpdateRequest.Builder().addFence(
                LOCATION_FENCE_KEY,
                locationFence,
                mPendingIntent
            ).build()
        ).addOnSuccessListener {
            Log.i(TAG, "Successfully registered fence")
        }.addOnFailureListener {
            Log.e(TAG, "Fence could not be registered: $it")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == ACTIVITY_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                registerActivityFence()
            } else {
                Toast.makeText(
                    this,
                    "You need to grant activity detection permissions for this app!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST) {
            // Checks if either both permissions are granted or FINE_LOCATION is granted and the device is running an Android version prior to 10
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) || (grantResults[0] == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT < 29)) {
                // Permission is granted
                registerLocationFence()
            } else {
                Toast.makeText(
                    this,
                    "You need to grant location permissions for this app!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
    private fun unregisterFence(fenceKey : String){
        Awareness.getFenceClient(this).updateFences(
            FenceUpdateRequest.Builder()
                .removeFence(fenceKey)
                .build()
        )
            .addOnSuccessListener { Log.i(TAG, "Fence $fenceKey  successfully unregistered.") }
            .addOnFailureListener { e -> Log.e(TAG, "Fence $fenceKey  not be unregistered: $e") }

    }


    fun onLocationPress(view: View) {
        Log.i(TAG, "Pressed location")
        if (needsLocationRuntimePermission()) {
            Log.i(TAG, "Requesting location permissions")
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), LOCATION_PERMISSION_REQUEST
            )
        } else {
            registerLocationFence()
        }
    }

    fun onActivityPress(view: View) {
        Log.i(TAG, "Pressed activity")
        /* val startTime = 13L * 60L * 60L * 1000L + 3L * 60L * 1000L
         val endTime = startTime + 60L * 60L * 1000L
         var timeFence = TimeFence.inDailyInterval(
             TimeZone.getDefault(),
             startTime,
             endTime
         )
        */
        if (needsActivityRuntimePermission()) {
            Log.i(TAG, "Requesting activity permissions")
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_PERMISSION_REQUEST
            )
        } else {
            registerActivityFence()
        }
    }

    fun onHeadphonesPress(view: View){
        var headphoneFence = HeadphoneFence.pluggingIn()
        Awareness.getFenceClient(this).updateFences(
            FenceUpdateRequest.Builder().addFence(
                HEADPHONE_FENCE_KEY,
                headphoneFence,
                mPendingIntent
            ).build()
        ).addOnSuccessListener {
            Log.i(TAG, "Successfully registered fence")
        }.addOnFailureListener {
            Log.e(TAG, "Fence could not be registered: $it")
        }
    }

    fun onNormalPress(view: View) {
        Log.i(TAG, "Pressed normal")
        mAudioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    fun onUnregisterPress(view: View) {
        Log.i(TAG, "Pressed unregister")
        unregisterFence(LOCATION_FENCE_KEY)
        unregisterFence(ACTIVITY_FENCE_KEY)
        unregisterFence(HEADPHONE_FENCE_KEY)
    }

}
