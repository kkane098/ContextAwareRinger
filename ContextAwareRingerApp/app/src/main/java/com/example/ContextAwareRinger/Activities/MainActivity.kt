package com.example.ContextAwareRinger.Activities

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.ContextAwareRinger.*
import com.example.ContextAwareRinger.Data.ActivityData
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

// Change UI to be loading screen that checks permissions
class MainActivity : FragmentActivity() {
    var tabLayout: TabLayout? = null
    val TAG = "MAIN_ACTIVITY"
    var viewpage: ViewPager? = null
    private lateinit var viewModel: TimeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabLayout = findViewById(R.id.tabLayout)
        Log.i(TAG, "tabLayout: " + tabLayout.toString() + " " + tabLayout!!.childCount)

        var timeTab = tabLayout!!.newTab().setText("Time")
        var locationTab = tabLayout!!.newTab().setText("Location")
        var activityTab = tabLayout!!.newTab().setText("Activity")
        var headphonesTab = tabLayout!!.newTab().setText("Headphones")


        tabLayout!!.addTab(timeTab)
        tabLayout!!.addTab(locationTab)
        tabLayout!!.addTab(activityTab)
        tabLayout!!.addTab(headphonesTab)

        val volumeMap = readVolumeMap(this, VOLUME_MAP_FILENAME).toMutableMap()
        Log.i(TAG, "map was $volumeMap")

        val fragmentAdapter = TabAdapter(supportFragmentManager, volumeMap)
        viewpage = findViewById(R.id.viewpager_main)
        viewpage!!.adapter = fragmentAdapter

        tabLayout!!.setupWithViewPager(viewpage)

        viewModel = ViewModelProviders.of(this)[TimeViewModel::class.java]
    }
}