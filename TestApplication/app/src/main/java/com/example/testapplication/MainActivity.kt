package com.example.testapplication

import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.os.Build
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat.getSystemService
import android.app.NotificationManager
import android.app.PendingIntent
import android.widget.Toast
import kotlin.concurrent.thread
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.*
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mAudioManager: AudioManager
    private lateinit var mPendingIntent: PendingIntent
    private val FENCE_KEY = "TEST_FENCE_KEY2"
    private val FENCE_RECEIVER_ACTION =
        "com.example.testapplication.FENCE_RECEIVER_ACTION"
    private val TAG = "TEST-CONTEXT"


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

    fun onSilentPress(view: View) {
        Log.i(TAG, "Pressed silent")
        mAudioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        Thread.sleep(1000)
        mAudioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        Toast.makeText(this, "New silence", Toast.LENGTH_SHORT).show()
    }

    fun onVibratePress(view: View) {
        Log.i(TAG, "Pressed vibrate")
        val startTime = 13L * 60L * 60L * 1000L + 3L * 60L * 1000L
        val endTime = startTime + 60L * 60L * 1000L
        var timeFence = TimeFence.inDailyInterval(
            TimeZone.getDefault(),
            startTime,
            endTime
        )
        var headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN)
        Awareness.getFenceClient(this).updateFences(
            FenceUpdateRequest.Builder().addFence(
                FENCE_KEY,
                timeFence,
                mPendingIntent
            ).build()
        ).addOnSuccessListener {
            Log.i(TAG, "Successfully registered fence!")
        }.addOnFailureListener {
            Log.e(TAG, "Fence could not be registered: $it")
        }
    }

    fun onNormalPress(view: View) {
        Log.i(TAG, "Pressed normal")
        mAudioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

}
