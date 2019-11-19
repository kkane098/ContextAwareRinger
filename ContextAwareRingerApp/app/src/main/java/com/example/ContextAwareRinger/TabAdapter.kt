@file:Suppress("DEPRECATION")

package com.example.ContextAwareRinger

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.ContextAwareRinger.Activities.ActivitiesActivity
import com.example.ContextAwareRinger.Activities.HeadphonesActivity
import com.example.ContextAwareRinger.Activities.LocationsActivity
import com.example.ContextAwareRinger.Activities.TimeActivity

class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TimeActivity()
            }
            1 -> ActivitiesActivity()
            2-> LocationsActivity()
            else -> {
                return HeadphonesActivity()
            }
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Time"
            1 -> "Activities"
            2-> "Location"
            else -> {
                return "Headphones"
            }
        }
    }
}