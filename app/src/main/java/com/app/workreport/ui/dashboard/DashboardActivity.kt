package com.app.workreport.ui.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.app.workreport.R
import com.app.workreport.databinding.ActivityDashboardBinding
import com.app.workreport.model.InvitationRequest
import com.app.workreport.ui.MainActivity
import com.app.workreport.ui.dashboard.ui.dashboard.DashboardViewModel
import com.app.workreport.ui.login.LoginViewModel
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var toolbarTitle: MaterialTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        navView.setupWithNavController(navController)

        binding.apply {
            toolbarTitle = toolbar.textTitle
        }

        if (AppPref.isLoggedIn)
            receivedData()

        binding.toolbar.btnLogout.setOnClickListener {
            this.logOutDialog {
                loginViewModel.logout()
            }
        }

        dashboardViewModel.invitationResponse.observe(this) { res ->
            res?.let {
                if (it.isNotEmpty() && REQUEST_TYPE == 1) {
                    REQUEST_TYPE = 0
                    binding.root.showSnackBar(it)
                    navController.navigate(R.id.navigation_dashboard)
                }
            }
        }
        dashboardViewModel.jobResponse.observe(this) { res ->
            if (res?.data?.isNotEmpty() == true) {
                res?.data?.get(0)?.let {
                    binding.progressLoadState.isVisible = false
                    when (it?.workerStatus) {
                        1 -> {
                            // if (it.assignedTo._id==AppPref.userID){
                            this.invitationDialog(
                                "${resources.getString(R.string.assigned_job)} ${it.job.name}\n\n\n${
                                    resources.getString(
                                        R.string.accept_reject_request
                                    )
                                }"
                            ) { status ->
                                // workerStatus: 1-sent, 2-accepted, 3-rejected
                                binding.progressLoadState.isVisible = true
                                REQUEST_TYPE = 1
                                dashboardViewModel?.addWorkInvitation(
                                    InvitationRequest(
                                        workPlanId = it._id,
                                        workerStatus = status
                                    )
                                )
                            }
                            //  }
                        }

                        2 -> {
                            this.invitationDialog(
                                resources.getString(R.string.job_already_accepted),
                                resources.getString(R.string.ok)
                            ) {}
                        }

                        3 -> {
                            this.invitationDialog(
                                resources.getString(R.string.job_already_rejected),
                                resources.getString(R.string.ok)
                            ) {}
                        }
                    }
                }
            } else {
                infoDialogError(ERROR_MESSAGE_TYPE, resources.getString(R.string.no_data)) {}
            }
        }

        xtnLog()
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.xtnToggleShowIf {
                (destination.id == R.id.navigation_dashboard ||
                        destination.id == R.id.navigation_report ||
                        destination.id == R.id.navigation_leave)
            }

            when (destination.id) {
                R.id.navigation_dashboard -> {
                    toolbarTitle.text = getString(R.string.title_dashboard)
                }

                R.id.navigation_report -> {
                    toolbarTitle.text = getString(R.string.report)
                }

                R.id.navigation_leave -> {
                    toolbarTitle.text = getString(R.string.leave)
                }
            }
            /* else if (destination.id == R.id.addLeaveFragment) {
                 toolbarTitle.text = getString(R.string.add_leave)
             }*/
        }

        loginViewModel.logOutStatus.observe(this) {
            if (it) {
                AppPref.isLogOut = true
                AppPref.isLoggedIn = false
                xtnNavigate<MainActivity>()
                finishAffinity()
            }
        }
    }

    private fun receivedData() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: String? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link.toString()
                    callApiUpdateInvitation(deepLink)
                } else {
                    callApiUpdateInvitation(intent?.data.toString())

                }
            }
            .addOnFailureListener(this) { e ->
                xtnLog("getDynamicLink:onFailure ${e.message}", "DynamicLink")
            }

    }

    fun callApiUpdateInvitation(deepLink: String) {
        deepLink?.let {
            if (it.contains("workPlanId") && it.contains("jobNumber")) {
                val workPlanId = it.split("workPlanId=")[1].split("&")[0]
                binding.progressLoadState.isVisible = true
                dashboardViewModel.getJobStatus(workPlanId)
            }
        }


    }



}