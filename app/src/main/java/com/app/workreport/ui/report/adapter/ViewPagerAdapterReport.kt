package com.app.workreport.ui.report.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.workreport.ui.report.ReportDetailFragment


private var NUM_TABS = 0
private var type = ""
private var jobId: String = ""
var complete: Boolean? = false
private var viewPager: ViewPager2? = null

class ViewPagerAdapterReport(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,

    ) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    fun setItemCount(count: Int) {
        NUM_TABS = count
    }

    fun isCompleted(isCompleted: Boolean) {
        complete = isCompleted
    }

    fun setType(mType: String) {
        type = mType
    }

    fun setJobId(mJobId: String) {
        jobId = mJobId
    }

    fun setViewPager(viewPager2: ViewPager2) {
        viewPager = viewPager2
    }

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return ReportDetailFragment.newInstance(position, type, complete)
    }
}