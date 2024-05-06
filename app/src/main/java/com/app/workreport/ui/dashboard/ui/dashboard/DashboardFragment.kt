package com.app.workreport.ui.dashboard.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.app.workreport.R
import com.app.workreport.common.adapter.LoaderStateAdapter
import com.app.workreport.container.BaseEvent
import com.app.workreport.databinding.FragmentDashboardBinding
import com.app.workreport.model.InvitationRequest
import com.app.workreport.service.ConnectivityReceiver
import com.app.workreport.ui.MainActivity
import com.app.workreport.ui.dashboard.ui.dashboard.adapter.AllTaskAdapter
import com.app.workreport.ui.dashboard.ui.dashboard.adapter.AllTaskPagingAdapter
import com.app.workreport.ui.dashboard.ui.work.WorkPlanActivity
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(),
    ConnectivityReceiver.ConnectivityReceiverListener {
    private lateinit var binding: FragmentDashboardBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private lateinit var adapterAllTaskPagingAdapter: AllTaskPagingAdapter
    private lateinit var adapterAllTaskAdapter: AllTaskAdapter
    private lateinit var loaderStateAdapter: LoaderStateAdapter
    private var filterType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION), RECEIVER_NOT_EXPORTED
        )

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentDashboardBinding.inflate(inflater, container, false)

        }
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun handleObservers() {
        handleView()

    }

    private fun callApi(query: String = "", status: String = "") {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            if (requireContext().isNetworkAvailable())
                dashboardViewModel.reportRepository.deleteAllTask()
            dashboardViewModel.getAllTaskList(query, status).catch { er ->
                xtnLog(er.message.toString(), TAGE)
            }.collectLatest {
                dashboardViewModel.baseEvent.postValue(BaseEvent.SUCCESS)
                binding.swipeRefresh.isRefreshing = false
                adapterAllTaskPagingAdapter.submitData(it)
            }


        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleView() {
        xtnLog(AppPref.accessToken.toString(), TAGE)
        // startServiceUploadImage()
        dashboardViewModel.invitationResponse.observe(viewLifecycleOwner) { res ->
            res?.let {
                if (it.isNotEmpty() && REQUEST_TYPE == 2) {
                    REQUEST_TYPE = 0
                    binding.progressLoadState.isVisible = false
                    adapterAllTaskPagingAdapter.refresh()
                    binding.root.showSnackBar(it)
                }
            }
        }

        binding.apply {
            /*used this class when internet not available*/
            adapterAllTaskAdapter =
                AllTaskAdapter(requireContext(), dashboardViewModel) { entity, _ ->
                    if (entity.progressStatus == IN_PROGRESS_STATUS || entity.progressStatus == WORK_PLAN_STATUS) {

                        navigateToWorkGallery(
                            entity.taskId,
                            entity.jobID,
                            entity.employeeId,
                            entity.progressStatus
                        )

                    } else {
                        openDialogNoInternet(
                            taskId = entity.taskId,
                            jobID = entity.jobID,
                            employeeId = entity.employeeId
                        )
                    }

                }

            searchView.search.hint = resources.getString(R.string.search_date)
            /*0 In-progress, 1 Work Plan, 2 submitted Site Report, 3 Manage Report, 4 Completed*/
            adapterAllTaskPagingAdapter =
                AllTaskPagingAdapter(requireContext(), dashboardViewModel) { entity, _ ->
                    if (requireContext().isNetworkAvailable()) {
                        if (entity.assign == 1) {
                            AppPref.leadID = entity.assignedTo?._id
                            AppPref.isSingleWorker=true
                            Log.e("TAG", "worker leadID: ${AppPref.leadID}}")
                        } else {
                            AppPref.leadID = entity.assignedTeamTo?.leadId
                            AppPref.isSingleWorker=false
                            Log.e("TAG", "team leadID: ${AppPref.leadID}}")
                        }
                        Log.e("TAG", "assign: ${entity.assign}")
                        if (entity?.workerStatus == 1) {
                            if (entity.assign == 1) {
                                callApiUpdateInvitation(entity.job.name, entity._id)
                            } else if (entity.assign == 2) {
                                if (AppPref.userID == entity.assignedTeamTo?.leadId) {
                                    callApiUpdateInvitation(entity.job.name, entity._id)
                                } else {
                                    requireContext().invitationDialog(
                                        resources.getString(R.string.team_lead_not_accepted_job_yet),
                                        resources.getString(R.string.ok)
                                    ) {}
                                    // Log.e("TAG", "teamLead and userLogin id are different: ask team lead to accept the job")

                                }

                            }
                        } else {
                            requireContext().xtnNavigate<WorkPlanActivity>(Bundle().apply {
                                putString(WORK_PLAN_ID, entity._id)
                                putString(JOB_ID, entity.job._id)
                                if (entity.assign == 1) {
                                    putString(EMPLOYEE_ID, entity.assignedTo?._id)
                                } else {
                                    putString(EMPLOYEE_ID, entity.assignedTeamTo?._id)
                                }
                                if (entity.job.progressStatus != WORK_PLAN_STATUS && entity.job.progressStatus != IN_PROGRESS_STATUS)
                                    putBoolean(IS_COMPLETED, true)
                                else
                                    putBoolean(IS_COMPLETED, false)
                            })

                        }

                    } else {
                        if (entity.job.progressStatus == IN_PROGRESS_STATUS || entity.job.progressStatus == WORK_PLAN_STATUS)
                            navigateToWorkGallery(
                                entity._id,
                                entity.job._id,
                                if (entity.assign == 1) {
                                    entity.assignedTo?._id ?: ""
                                } else {
                                    entity.assignedTeamTo?._id ?: ""
                                },
                                entity.job.progressStatus
                            )
                        else openDialogNoInternet(
                            entity._id,
                            entity.job._id,
                            if (entity.assign == 1) {
                                entity.assignedTo?._id ?: ""
                            } else {
                                entity.assignedTeamTo?._id ?: ""
                            }
                        )
                    }
                }

            loaderStateAdapter = LoaderStateAdapter { adapterAllTaskPagingAdapter.retry() }
            rvTaskList.apply {
                setHasFixedSize(true)
                adapter = if (requireContext().isNetworkAvailable())
                    adapterAllTaskPagingAdapter.withLoadStateFooter(loaderStateAdapter)
                else
                    adapterAllTaskAdapter
            }
            adapterAllTaskPagingAdapter.addLoadStateListener { loadState ->
                progressLoadState.isVisible = loadState.refresh is LoadState.Loading
                when (val currentState = loadState.refresh) {
                    is LoadState.Loading -> {
                        // progressLoadState.isVisible = loadState.refresh is LoadState.Loading
                    }

                    is LoadState.Error -> {
                        val extractedException = currentState.error
                        if (extractedException.message.toString().equals(EXPIRE_TOKEN, true)) {
                            requireActivity().infoDialogError(
                                ERROR_MESSAGE_TYPE,
                                resources.getString(R.string.msg_auth)
                            ) {
                                AppPref.isLogOut = true
                                AppPref.isLoggedIn = false
                                requireActivity().xtnNavigate<MainActivity>()
                                requireActivity().finishAffinity()
                            }
                        }
                        // SomeCatchableException
                        // xtnLog(extractedException.message.toString(), TAGE)
                    }

                    else -> {
                        //  xtnLog("extractedException.message".toString(), TAGE)
                    }
                }

            }

            searchView.filter.setOnClickListener {
                // showFilter = true

                if (requireContext().isNetworkAvailable()) {
                    searchView.search.apply {
                        setText("")
                        clearFocus()
                    }
                    FilterDialogFragment.newInstance(filterType) { type ->
                        filterType = type
                        when (type) {
                            0 -> {
                                //in progress
                                callApi(status = IN_PROGRESS_STATUS.toString())
                            }

                            1 -> {
                                //complete
                                callApi(status = COMPLETE_STATUS.toString())
                            }

                            2 -> {
                                callApi()
                            }

                            else -> {
                                filterType = -1
                            }
                        }
                    }.show(childFragmentManager, FilterDialogFragment.TAG)
                } else
                    openDialogNoInternet()

            }
            searchView.search.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.action == KeyEvent.ACTION_DOWN
                    && event.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    if (searchView.search.text.toString().trim()
                            .isNotEmpty() && requireContext().isNetworkAvailable()
                    )
                        callApi(query = searchView.search.text.toString().trim())
                    else {
                        openDialogNoInternet()
                        searchView.search.clearFocus()
                        searchView.search.setText("")
                    }

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
                        xtnLog(
                            "beforeTextChanged Start $start count $count after $after CharSequence $s",
                            "DashBoard"
                        )
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        xtnLog(
                            "onTextChanged Start $start count $count after $before CharSequence $s",
                            "DashBoard"
                        )
                        searchView.clearText.isVisible = count > 1
                        if (count < before && s!!.length < 1) {
                            callApi()
                            searchView.search.clearFocus()
                            searchView.search.hideKeyboard()
                        }


                    }

                    override fun afterTextChanged(s: Editable?) {
                        xtnLog("afterTextChanged Editable $s", "DashBoard")
                    }

                })
            }

            // searchView.search.setText(GetAppVersion(requireContext()))
            //  requireContext().xtnToast(GetAppVersion(requireContext()))
            swipeRefresh.setOnRefreshListener {
                filterType = -1
                handleObservers()
            }


            if (requireContext().isNetworkAvailable()) {
                when (filterType) {
                    0 -> {
                        //in progress
                        callApi(status = IN_PROGRESS_STATUS.toString())
                    }

                    1 -> {
                        //complete
                        callApi(status = COMPLETE_STATUS.toString())
                    }

                    2 -> {
                        callApi()
                    }

                    else -> callApi()
                }
                // callApi()

            } else {
                //  binding.root.showSnackBar(resources.getString(R.string.lost_internet))
                //  dashboardViewModel.baseEvent.postValue(BaseEvent.SUCCESS)
                progressLoadState.isVisible = false
                lifecycleScope.launch {

                    dashboardViewModel.reportRepository.getAllTask().catch {

                    }
                        .collect {
                            swipeRefresh.isRefreshing = false
                            if (it.isNotEmpty())
                                adapterAllTaskAdapter.swapList(it)
                        }

                }

            }


        }

        if (this::adapterAllTaskPagingAdapter.isInitialized) {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                adapterAllTaskPagingAdapter.loadStateFlow.collectLatest {
                    if (it.source.refresh is LoadState.NotLoading) {
                        if (binding.rvTaskList.adapter?.itemCount == 0)
                            requireActivity().infoDialogError(
                                ERROR_MESSAGE_TYPE,
                                resources.getString(R.string.no_data)
                            ) {}

                    }
                }
            }
        }
    }

    private fun navigateToWorkGallery(
        taskId: String,
        jobID: String,
        employeeId: String,
        progressStatus: Int
    ) {

        /*
                requireContext().xtnNavigate<CreateReportActivity>(Bundle().apply {
                    putString(WORK_PLAN_ID, taskId)
                    putString(JOB_ID, jobID)
                    putString(EMPLOYEE_ID, employeeId)
                    putString(NETWORK_STATUS, STATUS_OFFLINE)
                   // putSerializable(TARGET_DATA,null)
                })
        */

        requireContext().xtnNavigate<WorkPlanActivity>(Bundle().apply {
            putString(WORK_PLAN_ID, taskId)
            putString(JOB_ID, jobID)
            putString(EMPLOYEE_ID, employeeId)
            if (progressStatus != WORK_PLAN_STATUS && progressStatus != IN_PROGRESS_STATUS)
                putBoolean(IS_COMPLETED, true)
            else
                putBoolean(IS_COMPLETED, false)
        })


    }

    private fun openDialogNoInternet(
        taskId: String = "",
        jobID: String = "",
        employeeId: String = ""
    ) {
        requireActivity().infoDialogError(1, "") {
            if (requireContext().isNetworkAvailable() && taskId.isNotEmpty()) {

                requireContext().xtnNavigate<WorkPlanActivity>(Bundle().apply {
                    putString(WORK_PLAN_ID, taskId)
                    putString(JOB_ID, jobID)
                    putString(EMPLOYEE_ID, employeeId)
                    putBoolean(IS_COMPLETED, false)
                })

            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handleObservers()
    }

    /*
        private fun startServiceUploadImage() {
            try {
                lifecycleScope.launch {
                    dashboardViewModel.reportRepository.getAllImage().collect {imageList->
                        val finalList = imageList.filter { it.imageUrl.isEmpty() }
                        if (finalList.isNotEmpty()){
                            if (requireContext().isNetworkAvailable()){
                                val request = OneTimeWorkRequestBuilder<UserDataUploadWorker>().build()
                                WorkManager.getInstance(requireContext().applicationContext)
                                    .enqueue(request)
                            }
                        }
                    }
                }

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this

        if (requireContext().isNetworkAvailable())
            when (filterType) {
                0 -> {
                    //in progress
                    callApi(status = IN_PROGRESS_STATUS.toString())
                }

                1 -> {
                    //complete
                    callApi(status = COMPLETE_STATUS.toString())
                }

                2 -> {
                    callApi()
                }

                else -> {
                    callApi()
                }
                // callApi()
            }
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            xtnLog("You are offline", "checkNetwork")

        } else {
            //   startServiceUploadImage()
            // xtnLog("You are oNline","checkNetwork")

        }
    }

    fun callApiUpdateInvitation(jobNo: String, workPlanId: String) {
        //   deepLink?.let {
        // if (it.contains("workPlanId")&&it.contains("jobNumber")){
        requireActivity().invitationDialog(
            "${resources.getString(R.string.assigned_job)} $jobNo\n\n\n${
                resources.getString(
                    R.string.accept_reject_request
                )
            }"
        ) { status ->
            //workerStatus: 1-sent, 2-accepted, 3-rejected
            binding.progressLoadState.isVisible = true
            REQUEST_TYPE = 2
            //  loginViewModel.getReportDetail("64211fa0ab681facde6e0b77")
            dashboardViewModel.addWorkInvitation(
                InvitationRequest(
                    workPlanId = workPlanId,
                    workerStatus = status
                )
            )
            binding.progressLoadState.isVisible = false
        }
        //   }
        // }


    }


}