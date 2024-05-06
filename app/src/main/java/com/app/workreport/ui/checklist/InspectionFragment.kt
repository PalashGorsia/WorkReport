package com.app.workreport.ui.checklist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.app.workreport.data.model.CheckListTable
import com.app.workreport.databinding.FragmentInspectionBinding
import com.app.workreport.model.Data
import com.app.workreport.model.Data2
import com.app.workreport.model.Tab
import com.app.workreport.ui.checklist.adapter.ViewPagerAdapter
import com.app.workreport.ui.report.adapter.ViewPagerAdapterReport
import com.app.workreport.util.AppPref
import com.app.workreport.util.currentDateFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InspectionFragment(val jobID: String, val isCompleted: Boolean) : Fragment() {
    private lateinit var binding: FragmentInspectionBinding
    private val viewModel: CheckListViewModel by viewModels()
    var tabsArrayList: ArrayList<Tab> = arrayListOf()
    private var tabCount = 0

    companion object {
        fun newInstance(jobID: String, isCompleted: Boolean) =
            InspectionFragment(jobID, isCompleted)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (this::binding.isInitialized.not()) {
            binding = FragmentInspectionBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner = this
        handleObservers()


        return binding.root
    }

    private fun handleView() {
        binding.apply {
            //  ibBackward.visibility = View.GONE
            ///  ibForward.visibility = View.GONE

            //  WorkPlanActivity
            viewPager.isUserInputEnabled = false

            ibBackward.setOnClickListener {
                // pref?.setValue(JUMP_NEXT_PAGE, false)
                viewPager.setCurrentItem(viewPager.currentItem - 1, true)

            }
//todo dialog issue
            ibForward.setOnClickListener {
                if (binding.viewPager.currentItem > 0)
                    binding.ibBackward.visibility = View.VISIBLE

                //  pref?.setValue(JUMP_NEXT_PAGE, false)
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }



        if (isCompleted) {
            binding.progressBar.isVisible = true
            viewModel.getJobCompletedCheckList()
        } else {
            lifecycleScope.launch {
                try {
                    binding.progressBar.isVisible = true
                    val jobData = viewModel.getCheckListBasedOnJob()
                    viewModel.getJobCheckList()
                    /*  if (jobData.jobId.isNotEmpty()) {
                          setViewPagerArray(jobData.json, jobData.checkListId)
                      } else {
                          viewModel.getJobCheckList()
                      }*/
                } catch (e: Exception) {
                    viewModel.getJobCheckList()
                }
            }
        }

    }


    private fun handleObservers() {

        viewModel.checkListData.observe(viewLifecycleOwner) {
            val data = it?.checkListId?.fileData
            //  val mData = Gson().toJson(data)
            lifecycleScope.launch {
                val jobData = viewModel.getCheckListBasedOnJob()
                Log.e("TAG", "jobData: $jobData")
                if (jobData != null && jobData.jobId.isNotEmpty()) {
                    viewModel.updateCheckList(data!!)
                } else {
                    data?.let { da ->
                        viewModel.insertCheckList(
                            CheckListTable(
                                currentDateFormat,
                                da,
                                false,
                                it.checkListId._id,
                                jobID,
                                AppPref.employeeId ?: ""
                            )
                        )

                    }
                }
                //userId = JobId
            }
            lifecycleScope.launch {
                try {
                    it?.checkListId?._id?.let { setViewPagerArray(data, it) }
                } catch (e: Exception) {
                    Log.e("Tag", "Exception: Line 135 " + e.message )
                }

                // it?.checkListId?._id?.let { setViewPagerArray(data, it) }
            }

        }
        viewModel.checkListReportData.observe(viewLifecycleOwner) {
            val data = it.answerData
            lifecycleScope.launch {
                setViewPagerArray(data, "1256312")
            }

        }

    }

    private fun setViewPagerArray(it: String?, checkListId: String) {
        binding.progressBar.isVisible = false
        AppPref.checkListId = checkListId
        // AppPref.checkListId = checkListId
        val data: Any
        if (isCompleted) {
            data = Gson().fromJson(it, Data2::class.java)
            data?.let { it ->
                it.tabs.let {
                    tabsArrayList = it as ArrayList<Tab>
                }

            }
        } else {
            try {
                data = Gson().fromJson(it, Data::class.java)
                data?.let { it ->
                    it.tabs.let {
                        tabsArrayList = it as ArrayList<Tab>
                    }

                }
            } catch (e: Exception) {
                Log.e("Tag", "Exception: Line 175 " + e.message )
            }

        }

        // val data = Gson().fromJson(it, Data::class.java)
//        data?.let { it ->
//            it.tabs.let {
//                tabsArrayList = it as ArrayList<Tab>
//            }
//
//        }
        binding.isProgress = false
        try {
            if (isCompleted) {
                val adapter = ViewPagerAdapterReport(childFragmentManager, lifecycle)
                adapter.itemCount = tabsArrayList.size
                adapter.isCompleted(isCompleted)
                adapter.setType(it ?: "")
                adapter.setJobId(jobID)
                adapter.setViewPager(binding.viewPager)
                binding.viewPager.adapter = adapter
            } else {
                val adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
                adapter.itemCount = tabsArrayList.size
                adapter.setType(it ?: "")
                adapter.setJobId(jobID)
                adapter.setViewPager(binding.viewPager)
                binding.viewPager.adapter = adapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.viewPager.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.e("TestRavi", "currentPo: = $position")
                    binding.ibBackward.isVisible = position > 0
                    //val vv:Boolean = tabsArrayList.size-1 >= position
                    binding.ibForward.isVisible = tabsArrayList.size - 1 > position

                    /*                    if (tabsArrayList.size - 1 <= position)
                        binding.ibForward.isVisible = false
                    else
                        binding.ibForward.isVisible = true
*/

                    /*
                                        if (position>0){
                                            Log.e("TestRavi", "currentPo +0: = $position", )
                                         //   binding.ibForward.visibility = View.VISIBLE
                                            binding.ibBackward.visibility = View.VISIBLE
                                        }else{
                                            binding.ibBackward.visibility = View.INVISIBLE
                                        }
                    */
                    /*
                                        if (position==0){
                                            binding.ibBackward.visibility = View.INVISIBLE
                                            binding.ibForward.visibility = View.VISIBLE
                                        }else{
                                            binding.ibForward.visibility = View.VISIBLE
                                            binding.ibBackward.visibility = View.VISIBLE
                                        }
                    */
                    /*
                                        if (isYesterdayData){
                                          //  pref!!.setValue(VIEW_PAGER_INDEX_IN_PROGRESS, position)
                                        }
                                        else if(type == -1 || type == IN_PROGRESS.getValue())  {
                                           // pref!!.setValue(VIEW_PAGER_INDEX, position)

                                        }else if (statusComplete){
                                          //  pref!!.setValue(VIEW_PAGER_INDEX_COMPLETE, position)
                                        }
                    */
                    binding.selectedTabName.text = tabsArrayList[position].title
                }
            })

        }

        //set page No
        /*
                when {
                    statusComplete -> binding.viewPager.currentItem = pref!!.getValue(VIEW_PAGER_INDEX_COMPLETE, 0)!!
                    isYesterdayData -> binding.viewPager.currentItem = pref!!.getValue(VIEW_PAGER_INDEX_IN_PROGRESS, 0)!!
                    else -> binding.viewPager.currentItem = pref!!.getValue(VIEW_PAGER_INDEX, 0)!!
                }
        */
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->

        }.attach()


    }


}