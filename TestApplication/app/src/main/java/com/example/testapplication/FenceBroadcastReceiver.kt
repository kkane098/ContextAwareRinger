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
import android.content.BroadcastReceiver
import android.text.TextUtils
import android.widget.Toast
import kotlin.concurrent.thread
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.*
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

class FenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "TEST-RECEIVER"
    private val FENCE_KEY = "TEST_FENCE_KEY2"

    override fun onReceive(context: Context, intent: Intent) {

        Log.i(TAG, "Broadcast Received")

        Toast.makeText(context, "Broadcast Received by Receiver", Toast.LENGTH_LONG).show()
        val fenceState = FenceState.extract(intent)

        if (TextUtils.equals(fenceState.fenceKey, FENCE_KEY)) {
            when(fenceState.currentState){
                FenceState.TRUE -> {
                    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                    Log.i(TAG, "Fence state was true!")
                }
                FenceState.FALSE -> {
                    Log.i(TAG, "Fence state was false!")
                }
                FenceState.UNKNOWN -> {
                    Log.i(TAG, "Fence state unknown!")
                }
            }
        }

    }

}