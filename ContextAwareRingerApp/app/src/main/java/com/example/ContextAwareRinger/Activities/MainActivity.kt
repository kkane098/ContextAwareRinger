package com.example.ContextAwareRinger.Activities

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.example.ContextAwareRinger.Data.ActivityData
import com.example.ContextAwareRinger.R
import com.example.ContextAwareRinger.TabAdapter
import com.example.ContextAwareRinger.readFromFile
import com.example.ContextAwareRinger.writeToFile
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

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