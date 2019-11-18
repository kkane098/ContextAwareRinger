package com.example.testapplication

import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

class VolumeWorker(private val context: Context, private val params: WorkerParameters) :
    Worker(context, params) {
    private val TAG = "VolumeWorker"
    override fun doWork(): Result {
        Log.i(TAG, "Worker started!")
        val inputData = params.inputData
        val hour = inputData.getInt("hour", 0)
        val min = inputData.getInt("min", 0)
        val volume = inputData.getInt("volume", 0)
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
        rescheduleWork(hour, min, volume)
        return Result.success()
    }

    private fun rescheduleWork(hour: Int, min: Int, volume: Int) {
        Log.i(TAG, "Rescheduling work")
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR_OF_DAY, hour)
        dueDate.set(Calendar.MINUTE, min)
        dueDate.set(Calendar.SECOND, 0)
        dueDate.add(Calendar.MINUTE, 5)
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val inputData =
            workDataOf(
                "hour" to dueDate.get(Calendar.HOUR_OF_DAY),
                "min" to dueDate.get(Calendar.MINUTE),
                "volume" to volume
            )
        val dailyWorkRequest = OneTimeWorkRequestBuilder<VolumeWorker>().setInitialDelay(
            timeDiff,
            TimeUnit.MILLISECONDS
        ).setInputData(inputData).build()
        Log.i(TAG, "Enqueueing worker")
        WorkManager.getInstance(context)
            .enqueueUniqueWork("testWorkName", ExistingWorkPolicy.REPLACE, dailyWorkRequest)
    }
}