package com.app.workreport.ui.leave.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.workreport.ui.leave.AddLeaveFragment
import com.app.workreport.ui.leave.LeaveRequestFragment

private const val NUM_TABS = 2
class LeavesViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return LeaveRequestFragment()
            1 -> return AddLeaveFragment()
        }
        return LeaveRequestFragment()
    }
}