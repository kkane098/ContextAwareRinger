package com.example.ContextAwareRinger.Activities

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.ActivityData
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//TODO: Change UI to be loading screen that checks permissions
class MainActivity : FragmentActivity() {
    var tabLayout: TabLayout? = null
    val TAG = "MAIN_ACTIVITY"
    var viewpage: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //
        tabLayout = findViewById(R.id.tabLayout)
        Log.i(TAG, "tabLayout: " + tabLayout.toString() + " " + tabLayout!!.childCount)
        //val time = getTimeUntilNextTrigger(8, 0, WEEKEND)
        //Log.i(TAG, "time was $time")
        /* val activityData1 = ActivityData(1, "key1", AudioManager.RINGER_MODE_SILENT)
        val activityData2 = ActivityData(2, "key2", AudioManager.RINGER_MODE_VIBRATE)
        val activityDataList = listOf(activityData1, activityData2)
        writeActivityDataList(this, activityDataList, "testFile.txt")
        val readList = readActivityDataList(this, "testFile.txt")
        val size = readList.size
        Log.i(TAG, "size was $size")
        Log.i(TAG, readList[0].toString())
        Log.i(TAG, readList[1].toString())
        val volumeMap = mapOf("key1" to AudioManager.RINGER_MODE_SILENT, "key2" to AudioManager.RINGER_MODE_VIBRATE)
        writeVolumeMap(this, volumeMap, "testMapFile.txt")
        val readMap = readVolumeMap(this, "testMapFile.txt")
        Log.i(TAG, readMap.toString()) */

        var timeTab = tabLayout!!.newTab().setText("Time")
        var activityTab = tabLayout!!.newTab().setText("Activity")
        var locationTab = tabLayout!!.newTab().setText("Location")
        var headphonesTab = tabLayout!!.newTab().setText("Headphones")


        tabLayout!!.addTab(timeTab)
        tabLayout!!.addTab(activityTab)
        tabLayout!!.addTab(locationTab)
        tabLayout!!.addTab(headphonesTab)

        val fragmentAdapter = TabAdapter(supportFragmentManager)
        viewpage = findViewById(R.id.viewpager_main)
        viewpage!!.adapter = fragmentAdapter

        tabLayout!!.setupWithViewPager(viewpage)
    }
}