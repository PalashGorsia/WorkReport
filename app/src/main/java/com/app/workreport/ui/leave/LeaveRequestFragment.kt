package com.app.workreport.ui.leave

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.app.workreport.R
import com.app.workreport.common.adapter.LoaderStateAdapter
import com.app.workreport.container.BaseEvent
import com.app.workreport.databinding.FragmentLeaveRequestBinding
import com.app.workreport.ui.MainActivity
import com.app.workreport.ui.leave.adapter.LeavePagingAdapter
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class LeaveRequestFragment : Fragment() {

    private  val viewModel: LeaveRequestViewModel by activityViewModels()
    lateinit var binding: FragmentLeaveRequestBinding
    private lateinit var leavePagingAdapter: LeavePagingAdapter
    private lateinit var loaderStateAdapter: LoaderStateAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = FragmentLeaveRequestBinding.inflate(inflater)
            handleView()

        }
         binding.lifecycleOwner = this
        handleObservers()
        return binding.root
    }
//"type":"job"
    private fun handleObservers() {
        viewModel.responseDeleteLeave.observe(viewLifecycleOwner){
            if (it){
                leavePagingAdapter.refresh()
            }
        }
    //workPlanId

        viewModel.resMessDeleteLeave.observe(viewLifecycleOwner){
            if (!it.isNullOrEmpty())
                binding.root.showSnackBar(it)
        }
        viewModel.responseHolidays.observe(viewLifecycleOwner){
            val today = LocalDate.now().toString()
            val month  = Date().month
            var po = 0
            for (i in it.indices){
                val mon = convertDateFormat(it[i].date, YYYY_MM_DD_T_HH_MM_SS_24, MM).toInt()
                val date = convertDateFormat(it[i].date, YYYY_MM_DD_T_HH_MM_SS_24, YYYY_MM_DD)
                if (date.equals(today,true)){
                    po = i
                    break
                }else if (month==mon){
                    po = i
                   // break
                }
            }
            callApi(po)
        }
        handleView()
    }

    private fun handleView() {
        binding.apply {
            leavePagingAdapter = LeavePagingAdapter {
                    entity, _ ->
                requireActivity().logOutDialog(resources.getString(R.string.info_delete_leave)) {
                    viewModel.deleteLave(entity._id)
                }
            }

            loaderStateAdapter = LoaderStateAdapter { leavePagingAdapter.retry() }
            rvTaskList.apply {
                setHasFixedSize(true)
                adapter = leavePagingAdapter
            }
            leavePagingAdapter.addLoadStateListener { loadState ->
                progressLoadState.isVisible = loadState.refresh is LoadState.Loading
                when (val currentState = loadState.refresh) {
                    is LoadState.Loading -> {
                        // progressLoadState.isVisible = loadState.refresh is LoadState.Loading
                    }
                    is LoadState.Error -> {
                        val extractedException = currentState.error
                        if (extractedException.message.toString().equals(EXPIRE_TOKEN,true)){
                            requireActivity().infoDialogError(ERROR_MESSAGE_TYPE,resources.getString(
                                R.string.msg_auth)){
                                AppPref.isLogOut =true
                                AppPref.isLoggedIn =false
                                requireActivity().xtnNavigate<MainActivity>()
                                requireActivity().finishAffinity()
                            }
                        }
                        // SomeCatchableException
                        // xtnLog(extractedException.message.toString(), TAGE)
                    }
                    else ->{
                        //  xtnLog("extractedException.message".toString(), TAGE)
                    }
                }

            }

            swipeRefresh.setOnRefreshListener {
                handleObservers()
            }

            if (requireContext().isNetworkAvailable()){
                viewModel.getHolidays()
            }else{
                progressLoadState.isVisible = false
            }


        }
        if(this::leavePagingAdapter.isInitialized) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                leavePagingAdapter.loadStateFlow.collectLatest {
                    if (it.source.refresh is LoadState.NotLoading) {
                        if ( binding.rvTaskList.adapter?.itemCount == 0)
                            binding.noData.text = resources.getString(R.string.no_data)
                        else
                            binding.noData.text = ""

                    }
                }
            }
        }



    }

    private fun callApi(po:Int) {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            var pageNo = 1
            if (po>3){
                pageNo = (po/5)+1
            }

            if (requireContext().isNetworkAvailable())
            viewModel.getApplyLeaveList(pageNo).catch { er->
                xtnLog(er.message.toString(), TAGE)
            } .collectLatest {
                viewModel.baseEvent.postValue(BaseEvent.SUCCESS)
                binding.swipeRefresh.isRefreshing = false
                leavePagingAdapter.submitData(it)
            }
        }

    }
}