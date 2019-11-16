package com.example.ContextAwareRinger

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

    override fun onReceive(context: Context, intent: Intent) {

        Log.i(TAG, "Broadcast Received")

        Toast.makeText(context, "Broadcast Received by Receiver", Toast.LENGTH_LONG).show()
        val fenceState = FenceState.extract(intent)
        val fenceKey = fenceState.fenceKey
        // TODO: check map of keys to ringer modes and change ringer mode accordingly
    }
}