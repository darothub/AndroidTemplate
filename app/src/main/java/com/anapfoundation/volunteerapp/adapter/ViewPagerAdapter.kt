package com.anapfoundation.volunteerapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.anapfoundation.volunteerapp.ui.ApprovedReportFragment
import com.anapfoundation.volunteerapp.ui.UnapprovedReportFragment

/**
 * A simple pager adapter that represents 2 Fragment  objects, in
 * sequence.
 */
class ViewPagerAdapter(fm:FragmentManager, lifecycle:Lifecycle) : FragmentStateAdapter(fm, lifecycle) {

    val fragments = arrayListOf<Fragment>(UnapprovedReportFragment(), ApprovedReportFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}