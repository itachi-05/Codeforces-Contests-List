package com.hoMS.hms.repos

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hoMS.hms.ui.activities.ActivityMain
import com.hoMS.hms.ui.fragments.AppointmentFragment
import com.hoMS.hms.ui.fragments.DigitalHealthCardFragment
import com.hoMS.hms.ui.fragments.HomeFragment
import com.hoMS.hms.ui.fragments.SettingsFragment


class MyAdapter(fragmentActivity: ActivityMain) : FragmentStateAdapter(fragmentActivity) {
    private val fragmentList = arrayListOf(
        HomeFragment(),
        AppointmentFragment(),
        DigitalHealthCardFragment(),
        SettingsFragment()
    )

    override fun getItemCount() = fragmentList.size

    override fun createFragment(position: Int) = fragmentList[position]
}
