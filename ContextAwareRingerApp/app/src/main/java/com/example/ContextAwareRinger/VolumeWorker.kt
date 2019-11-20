package com.example.ContextAwareRinger

import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class VolumeWorker(private val context: Context, private val params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        Log.i(TAG, "Worker started!")
        val inputData = params.inputData
        val hour = inputData.getInt("hour", 0)
        val min = inputData.getInt("min", 0)
        val day = inputData.getInt("day", 0)
        val volume = inputData.getInt("volume", 0)
        val workKey = inputData.getString("key")!!
        Log.i(TAG, "hour was $hour")
        Log.i(TAG, "min was $min")
        Log.i(TAG, "volume was $volume")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (volume == AudioManager.RINGER_MODE_SILENT) {
            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            Thread.sleep(1000)
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        } else {
            audioManager.ringerMode = volume
        }
        Log.i(TAG, "Worker changed volume!")
        enqueueTimeWorker(context, hour, min, day, volume, workKey)
        return Result.success()
    }

    companion object  {
        private val TAG = "VolumeWorker"
    }
}