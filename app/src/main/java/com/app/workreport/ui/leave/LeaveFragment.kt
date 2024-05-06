package com.app.workreport.ui.leave

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.app.workreport.R
import com.app.workreport.databinding.FragmentLeaveBinding
import com.app.workreport.ui.leave.adapter.LeavesViewPagerAdapter

class LeaveFragment : Fragment() {
private lateinit var binding : FragmentLeaveBinding
    companion object {
        fun newInstance() = LeaveFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = FragmentLeaveBinding.inflate(inflater)
        }
        handleView()
        binding.lifecycleOwner = this

        return binding.root
    }

    private fun handleView() {
        val array = arrayOf(
            getString(R.string.leave),
            getString(R.string.apply)
        )

        binding.apply {
            pager.isUserInputEnabled = false
            val adapter = LeavesViewPagerAdapter(childFragmentManager, lifecycle)
            pager.adapter = adapter

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                tab.text = array[position]
            }.attach()
        }
    }
}