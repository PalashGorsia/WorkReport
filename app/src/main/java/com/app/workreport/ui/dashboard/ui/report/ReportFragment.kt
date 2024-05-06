package com.app.workreport.ui.dashboard.ui.report

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.app.workreport.R
import com.app.workreport.common.adapter.LoaderStateAdapter
import com.app.workreport.container.BaseEvent
import com.app.workreport.databinding.FragmentReportBinding
import com.app.workreport.ui.dashboard.ui.dashboard.DashboardViewModel
import com.app.workreport.ui.dashboard.ui.dashboard.adapter.AllCompleteTaskPagingAdapter
import com.app.workreport.ui.dashboard.ui.work.WorkPlanActivity
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var adapterAllCompleteTaskPagingAdapter: AllCompleteTaskPagingAdapter
    private lateinit var loaderStateAdapter: LoaderStateAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = FragmentReportBinding.inflate(inflater, container, false)
            // handleView()
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun handleView() {
        binding.apply {
            searchView.filter.isVisible =false
            adapterAllCompleteTaskPagingAdapter = AllCompleteTaskPagingAdapter(requireContext()){
                    entity, pos ->

                requireContext().xtnNavigate<WorkPlanActivity>(Bundle().apply {
                    putString(WORK_PLAN_ID, entity._id)
                    putString(JOB_ID,entity.job._id)
                    putString(EMPLOYEE_ID,entity.assignedTo?._id)
                    putBoolean(IS_COMPLETED,true)
                })

/*
                requireContext().xtnNavigate<WorkReportActivity>(Bundle().apply {
                    putString(WORK_PLAN_ID, entity._id)
                  //  putString(REPORT_ID, entity.report._id)
                })
*/
            }
            loaderStateAdapter = LoaderStateAdapter { adapterAllCompleteTaskPagingAdapter.retry() }

            rvComplete.apply {
                setHasFixedSize(true)
                adapter = adapterAllCompleteTaskPagingAdapter.withLoadStateFooter(loaderStateAdapter)
            }
            adapterAllCompleteTaskPagingAdapter.addLoadStateListener { loadState ->
                progressLoadState.isVisible = loadState.refresh is LoadState.Loading
            }

            searchView.search.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (searchView.search.text.toString().trim().isNotEmpty())
                        callApi(query= searchView.search.text.toString().trim(),status = COMPLETE_STATUS.toString())
                    searchView.search.hideKeyboard()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            searchView.clearText.setOnClickListener {
                searchView.search.apply {
                    setText("")
                    clearFocus()
                    hideKeyboard()
                    callApi()
                }

            }

            searchView.search.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        xtnLog("beforeTextChanged Start $start count $count after $after CharSequence $s","DashBoard")
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        xtnLog("onTextChanged Start $start count $count after $before CharSequence $s","DashBoard")
                        //count = 6 after = 5 go hed
                        //back count 6 after 7 back
                        searchView.clearText.isVisible =count>1
                        if (count<before&& s!!.length<1){
                            callApi()
                            searchView.search.clearFocus()
                            searchView.search.hideKeyboard()
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        xtnLog("afterTextChanged Editable $s","DashBoard")
                    }

                })
            }

            swipeRefresh.setOnRefreshListener {
                handleObservers()
            }

        }
/*
        binding.viewClick.cardClick.setOnClickListener {
            requireActivity().xtnNavigate<WorkReportActivity>()

        }
*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        handleObservers()
    }

    private fun handleObservers() {
        handleView()
        if (requireContext().isNetworkAvailable()){
            callApi()

        }else{
            binding.rvComplete.showSnackBar(resources.getString(R.string.lost_internet))
        }

        if(this::adapterAllCompleteTaskPagingAdapter.isInitialized) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                adapterAllCompleteTaskPagingAdapter.loadStateFlow.collectLatest {
                    if (it.source.refresh is LoadState.NotLoading) {
                        if ( binding.rvComplete.adapter?.itemCount == 0)
                            requireActivity().infoDialogError(ERROR_MESSAGE_TYPE,resources.getString(R.string.no_data)){}

                    }
                    //
                }
            }
        }


    }

    private fun callApi(query:String="",status:String="") {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            dashboardViewModel.getAllTaskList(query, COMPLETE_STATUS.toString()).collectLatest {
                dashboardViewModel.baseEvent.postValue(BaseEvent.SUCCESS)
                binding.swipeRefresh.isRefreshing = false
                adapterAllCompleteTaskPagingAdapter.submitData(it)
            }
        }

    }


}