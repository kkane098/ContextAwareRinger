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
        
        val fenceState = FenceState.extract(intent)
        val fenceKey = fenceState.fenceKey
        val volumeMap = readVolumeMap(context, VOLUME_MAP_FILENAME)
        Log.i(TAG, "Map in receiver was $volumeMap")
        if(volumeMap.containsKey(fenceKey)){
            val ringerMode = volumeMap[fenceKey]
            when (fenceState.currentState) {
                FenceState.TRUE -> {
                    val audioManager =
                        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    if (ringerMode == AudioManager.RINGER_MODE_SILENT) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        Thread.sleep(1000)
                        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                    } else {
                        audioManager.ringerMode = ringerMode!!
                    }
                    Log.i(TAG, "Fence $fenceKey was true!")
                }
                FenceState.FALSE -> {
                    Log.i(TAG, "Fence $fenceKey was false!")
                }
                FenceState.UNKNOWN -> {
                    Log.i(TAG, "Fence $fenceKey state unknown!")
                }
            }
        }
    }
}