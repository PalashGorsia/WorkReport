package com.app.workreport.ui.checklist.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.app.workreport.ui.checklist.PersonalHealthFragment

private var NUM_TABS = 0
private var type = ""
private var viewPager2:ViewPager2?=null
private var jobId: String = ""
class ViewPagerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    fun setItemCount(count : Int) {
         NUM_TABS = count
    }

    fun setType(mtype : String) {
        type = mtype
    }

    fun setJobId(mJobId:String){
       jobId = mJobId
    }

    fun setViewPager(viewPager:ViewPager2){
        viewPager2 = viewPager
    }
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
       // xtnLog("call createFragment view pager ","TestFlow")
        return PersonalHealthFragment.newInstance(position, type, viewPager2!!, jobId)
    }
}