@file:Suppress("DEPRECATION")

package com.example.ContextAwareRinger

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.ContextAwareRinger.Activities.ActivitiesFragment
import com.example.ContextAwareRinger.Activities.HeadphonesFragment
import com.example.ContextAwareRinger.Activities.LocationsFragment
import com.example.ContextAwareRinger.Activities.TimeFragment

class TabAdapter(fm: FragmentManager, private val volumeMap: MutableMap<String, Int>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TimeFragment()
            }
            1 -> LocationsFragment(volumeMap)
            2-> ActivitiesFragment(volumeMap)
            else -> {
                return HeadphonesFragment(volumeMap)
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Time"
            1 -> "Location"
            2-> "Activity"
            else -> {
                return "Headphones"
            }
        }
    }
}