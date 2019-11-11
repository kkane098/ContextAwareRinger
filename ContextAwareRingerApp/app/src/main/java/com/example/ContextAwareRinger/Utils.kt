package com.example.ContextAwareRinger

import android.util.Log
import java.io.*
val TAG = "UTILS"

//Example usage of these functions, the following code will write an object to a file then read it back:
//  val activityData = ActivityData(1, "test", 1)
//  writeToFile(filesDir.toString() + "/out.tmp", activityData as Object)
//  val data = readFromFile(filesDir.toString() + "/out.tmp") as ActivityData

//TODO: If we need to write a list or map to files we should create helper methods for that

fun writeToFile(fileName:String?, obj:Object?) {
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

fun readFromFile(fileName: String?):Any? {
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