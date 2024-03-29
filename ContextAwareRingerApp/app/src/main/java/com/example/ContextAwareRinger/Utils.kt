package com.example.ContextAwareRinger

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Spinner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.ContextAwareRinger.Data.ActivityData
import com.example.ContextAwareRinger.Data.HeadphonesData
import com.example.ContextAwareRinger.Data.LocationData
import com.example.ContextAwareRinger.Data.TimeData
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.serialization.*
import kotlinx.serialization.internal.IntSerializer
import kotlinx.serialization.internal.StringSerializer
import kotlinx.serialization.json.*
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter


val TAG = "UTILS"
val FENCE_RECEIVER_ACTION = "com.example.ContextAwareRinger.FENCE_RECEIVER_ACTION"
val DAILY = -1
val WEEKDAY = -2
val WEEKEND = -3
val VOLUME_MAP_FILENAME = "volumeMap.txt"
val LOCATION_LIST_FILENAME = "locationList.txt"
val TIME_LIST_FILENAME = "timeList.txt"
val HEADPHONES_LIST_FILENAME = "headphonesList.txt"
val ACTIVITY_LIST_FILENAME = "activityList.txt"

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

fun getTimeUntilNextTrigger(hour: Int, min: Int, day: Int): Long {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance()
    dueDate.set(Calendar.HOUR_OF_DAY, hour)
    dueDate.set(Calendar.MINUTE, min)
    dueDate.set(Calendar.SECOND, 0)
    when (day) {
        DAILY -> {
            // If time is in past, schedule for tomorrow
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
        }
        WEEKDAY -> {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            // If today is Mon - Thurs and time is in past, schedule for tomorrow
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.THURSDAY && dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            // If today is Sun, schedule for Mon
            else if (dayOfWeek == Calendar.SUNDAY) {
                dueDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            }
            // If today is Fri and time is in past or today is Sat, schedule for Mon next week
            else if (dayOfWeek == Calendar.FRIDAY && dueDate.before(currentDate) || dayOfWeek == Calendar.SATURDAY) {
                dueDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                dueDate.add(Calendar.DAY_OF_YEAR, 7)
            }
        }
        WEEKEND -> {
            val dayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK)
            // If today is Mon - Fri, schedule for Sat
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                dueDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
            // If today is Sat and time is in past, schedule for tomorrow
            if (dayOfWeek == Calendar.SATURDAY && dueDate.before(currentDate)) {
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            // If today is Sun and time is in past, schedule for Sat
            if (dayOfWeek == Calendar.SUNDAY && dueDate.before(currentDate)) {
                dueDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            }
        }
        // On a day of the week
        else -> {
            dueDate.set(Calendar.DAY_OF_WEEK, day)
            // If time is in past, schedule for next week
            if (dueDate.before(currentDate)) {
                dueDate.add(Calendar.DAY_OF_YEAR, 7)
            }
        }
    }
    return dueDate.timeInMillis - currentDate.timeInMillis
}

// Day will be one of the day of the week constants defined in calendar (Sun = 1 -> Sat = 7) EXCEPT for DAILY(-1), WEEKDAY(-2), and WEEKEND(-3)
fun enqueueTimeWorker(
    context: Context,
    hour: Int,
    min: Int,
    day: Int,
    volume: Int,
    workKey: String
) {
    val timeDiff = getTimeUntilNextTrigger(hour, min, day)
    val inputData =
        workDataOf(
            "hour" to hour,
            "min" to min,
            "day" to day,
            "volume" to volume,
            "key" to workKey
        )
    val volumeWorkRequest = OneTimeWorkRequestBuilder<VolumeWorker>().setInitialDelay(
        timeDiff,
        TimeUnit.MILLISECONDS
    ).setInputData(inputData).build()
    Log.i(TAG, "Enqueueing worker")
    WorkManager.getInstance(context)
        .enqueueUniqueWork(workKey, ExistingWorkPolicy.REPLACE, volumeWorkRequest)
}

fun removeTimeWorker(context: Context, workKey: String){
    WorkManager.getInstance(context).cancelUniqueWork(workKey)
}

private fun writeString(context: Context, data: String, fileName: String){
    try {

        Log.i(TAG, "wrote $data")
        val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        val pw = PrintWriter(BufferedWriter(OutputStreamWriter(fos)))

        pw.println(data)

        pw.close()

    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
}

private fun readString(context: Context, fileName: String): String{
    val fis = context.openFileInput(fileName)
    val br = BufferedReader(InputStreamReader(fis))

    val jsonString = br.readLine()
    Log.i(TAG, "read $jsonString")

    br.close()
    return jsonString
}

fun writeActivityDataList(context: Context, list: List<ActivityData>, fileName: String) {
    val json = Json(JsonConfiguration.Stable)
    val jsonListString = json.stringify(ActivityData.serializer().list, list)

    writeString(context, jsonListString, fileName)
}

fun readActivityDataList(context: Context, fileName: String): List<ActivityData>{
    val json = Json(JsonConfiguration.Stable)
    return try {
        val jsonListString = readString(context, fileName)
        json.parse(ActivityData.serializer().list, jsonListString)
    }
    catch (e: FileNotFoundException){
        listOf()
    }
}

fun writeTimeDataList(context: Context, list: List<TimeData>, fileName: String){
    val json = Json(JsonConfiguration.Stable)
    val jsonListString = json.stringify(TimeData.serializer().list, list)

    writeString(context, jsonListString, fileName)
}

fun readTimeDataList(context: Context, fileName: String): List<TimeData>{
    val json = Json(JsonConfiguration.Stable)
    return try {
        val jsonListString = readString(context, fileName)
        json.parse(TimeData.serializer().list, jsonListString)
    }
    catch (e: FileNotFoundException){
        listOf()
    }
}

fun writeLocationDataList(context: Context, list: List<LocationData>, fileName: String){
    val json = Json(JsonConfiguration.Stable)
    val jsonListString = json.stringify(LocationData.serializer().list, list)

    writeString(context, jsonListString, fileName)
}

fun readLocationDataList(context: Context, fileName: String): List<LocationData>{
    val json = Json(JsonConfiguration.Stable)
    return try {
        val jsonListString = readString(context, fileName)
        json.parse(LocationData.serializer().list, jsonListString)
    }
    catch (e: FileNotFoundException){
        listOf()
    }
}

fun writeHeadphoneDataList(context: Context, list: List<HeadphonesData>, fileName: String){
    val json = Json(JsonConfiguration.Stable)
    val jsonListString = json.stringify(HeadphonesData.serializer().list, list)

    writeString(context, jsonListString, fileName)
}

fun readHeadphonesDataList(context: Context, fileName: String): List<HeadphonesData>{
    val json = Json(JsonConfiguration.Stable)
    return try {
        val jsonListString = readString(context, fileName)
        json.parse(HeadphonesData.serializer().list, jsonListString)
    }
    catch (e: FileNotFoundException){
        listOf()
    }
}

fun writeVolumeMap(context: Context, map : Map<String, Int>, fileName: String){
    val json = Json(JsonConfiguration.Stable)
    val jsonMapString = json.stringify((StringSerializer to IntSerializer).map, map)

    writeString(context, jsonMapString, fileName)
}

fun readVolumeMap(context: Context, fileName: String): Map<String, Int>{
    val json = Json(JsonConfiguration.Stable)

    return try {
        val jsonMapString = readString(context, fileName)
        json.parse((StringSerializer to IntSerializer).map, jsonMapString)
    }
    catch (e: FileNotFoundException){
        mapOf()
    }
}

// Util function that returns an int based on the day selected in Time Fragment
fun getRepetitionInterval(spinner : Spinner) : Int {
    val text = spinner.selectedItem.toString()
    when (text) {
        "Daily" -> return com.example.ContextAwareRinger.DAILY
        "Weekends" -> return com.example.ContextAwareRinger.WEEKEND
        "Week Days" -> return com.example.ContextAwareRinger.WEEKDAY
        "Monday" -> return Calendar.MONDAY
        "Tuesday" -> return Calendar.TUESDAY
        "Wednesday" -> return Calendar.WEDNESDAY
        "Thursday" -> return Calendar.THURSDAY
        "Friday" -> return Calendar.FRIDAY
        "Saturday" -> return Calendar.SATURDAY
        "Sunday" -> return Calendar.SUNDAY
    }

    //Should never reach this point
    throw Exception("No valid value for repetition interval found")

}