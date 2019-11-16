package com.example.ContextAwareRinger

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.*
import java.io.*

val TAG = "UTILS"
val FENCE_RECEIVER_ACTION = "com.example.ContextAwareRinger.FENCE_RECEIVER_ACTION"

//Example usage of these functions, the following code will write an object to a file then read it back:
//  val activityData = ActivityData(1, "test", 1)
//  writeToFile(filesDir.toString() + "/out.tmp", activityData as Object)
//  val data = readFromFile(filesDir.toString() + "/out.tmp") as ActivityData

//TODO: If we need to write a list or map to files we should create helper methods for that

fun writeToFile(fileName: String?, obj: Object?) {
    try {
        // write object to file
        val fileOut = FileOutputStream(fileName)
        val objectOut = ObjectOutputStream(fileOut)
        objectOut.writeObject(obj)
        objectOut.close()
        Log.i(TAG, "Finished writing to file")

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }

}

fun readFromFile(fileName: String?): Any? {
    try {
        val fileInput = FileInputStream(fileName)
        val objectInput = ObjectInputStream(fileInput)
        val obj = objectInput.readObject() ?: return null
        Log.i(TAG, "Finished reading from file, returning non null object")
        return obj

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return null
}

private fun getPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, FenceBroadcastReceiver::class.java)
    intent.action = FENCE_RECEIVER_ACTION
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}

private fun registerFence(
    context: Context,
    fenceKey: String,
    awarenessFence: AwarenessFence
) {
    Awareness.getFenceClient(context).updateFences(
        FenceUpdateRequest.Builder().addFence(
            fenceKey,
            awarenessFence,
            getPendingIntent(context)
        ).build()
    ).addOnSuccessListener {
        Log.i(TAG, "Successfully registered fence $fenceKey")
    }.addOnFailureListener {
        Log.e(TAG, "Fence $fenceKey could not be registered: $it")
    }
}

/* Example usage for these functions called from an activity:
   registerDetectedActivityFence(this, DetectedActivityFence.ON_FOOT, "EXAMPLE_KEY")
   Activities will need to acquire permissions to run registerDetectedActivityFence and registerLocationFence
 */

fun registerLocationFence(
    context: Context,
    lat: Double,
    long: Double,
    radius: Double,
    fenceKey: String
) {
    val locationFence = LocationFence.`in`(lat, long, radius, 60 * 1000L)
    registerFence(context, fenceKey, locationFence)
}

fun registerDetectedActivityFence(context: Context, activityType: Int, fenceKey: String) {
    val detectedActivityFence = DetectedActivityFence.during(activityType)
    registerFence(context, fenceKey, detectedActivityFence)
}

fun registerHeadphoneInFence(context: Context, fenceKey: String) {
    val headphoneInFence = HeadphoneFence.pluggingIn()
    registerFence(context, fenceKey, headphoneInFence)
}

fun registerHeadphoneOutFence(context: Context, fenceKey: String) {
    val headphoneOutFence = HeadphoneFence.unplugging()
    registerFence(context, fenceKey, headphoneOutFence)
}

fun unregisterFence(context: Context, fenceKey: String) {
    Awareness.getFenceClient(context).updateFences(
        FenceUpdateRequest.Builder()
            .removeFence(fenceKey)
            .build()
    )
        .addOnSuccessListener { Log.i(TAG, "Fence $fenceKey  successfully unregistered.") }
        .addOnFailureListener { e -> Log.e(TAG, "Fence $fenceKey  not be unregistered: $e") }

}