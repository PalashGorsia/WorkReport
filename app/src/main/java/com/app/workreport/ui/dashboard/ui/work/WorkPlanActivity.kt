package com.app.workreport.ui.dashboard.ui.work

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.app.workreport.R
import com.app.workreport.databinding.ActivityWorkPlanBinding
import com.app.workreport.model.AdditionalInfo
import com.app.workreport.model.AnswerData2
import com.app.workreport.model.AnswerDataWithoutAdditionaInfo
import com.app.workreport.ui.MainActivity
import com.app.workreport.ui.checklist.CheckListViewModel
import com.app.workreport.ui.checklist.InspectionFragment
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkPlanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWorkPlanBinding
    private val viewModel: CheckListViewModel by viewModels()
    private var workPlanId = ""
    private var jobId = ""
    private var employeeId = ""
    private var isCompleted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.textTitle.text = getString(R.string.checklist)
        try {
            intent?.extras?.let {
                workPlanId = it.getString(WORK_PLAN_ID) ?: ""
                jobId = it.getString(JOB_ID, "")
                employeeId = it.getString(EMPLOYEE_ID, "")
                isCompleted = it.getBoolean(IS_COMPLETED)
            }
            AppPref.employeeId = employeeId
            AppPref.jobId = jobId

        } catch (e: Exception) {

        }
        handleView()
        handleObservers()
        try {

            //new flow
            supportFragmentManager.beginTransaction().add(
                binding.container.id,
                InspectionFragment.newInstance(jobId, isCompleted), "ReportInfoFragment"
            ).commit()
            //old flow
            //  supportFragmentManager.beginTransaction().add(binding.container.id,ReportInfoFragment.newInstance(workPlanId,true),"ReportInfoFragment").commit()
        } catch (e: Exception) {
            finish()
        }
    }

    override fun onBackPressed() {
        lifecycleScope.launch {
            if (AppPref.isAnswerSelected) {
                backButtonAction()
            } else {
                //   Toast.makeText(this@WorkPlanActivity, "getBack", Toast.LENGTH_SHORT).show()
                super.onBackPressed()

            }

        }


    }

    private fun backButtonAction() {


        saveChangesAlertDialog(getString(R.string.do_you_want_to_save_changes)) {
            if (it == getString(R.string.yes)) {
                onYesClicked()
            } else {
                super.onBackPressed()
                AppPref.isAnswerSelected = false

            }
        }
    }


    private fun onYesClicked() {
        lifecycleScope.launch {
            val answerList = viewModel.getAnswerData()
            val answerData2 = AnswerData2(answerList)
            Log.e("TAG", "answerData2: $answerData2")
            val answerList2ToJson = Gson().toJson(answerData2)
            Log.e("TAG", "answerList2ToJson: $answerList2ToJson")
            val additionInfoToJson = Gson().toJson(AdditionalInfo("", ""))
            // var result: AnswerDataWithoutAdditionaInfo? = null
            val result = AnswerDataWithoutAdditionaInfo(
                answerList2ToJson,
                "string",
                AppPref.jobId ?: "",
                getLocale(),
                AppPref.employeeId ?: "",
                1
            )
            viewModel.addCheckListNew(result)

        }

    }

    private fun handleView() {
        binding.apply {
            toolbar.btnLogout.setOnClickListener {
                this@WorkPlanActivity.logOutDialog {
                    AppPref.isLogOut = true
                    AppPref.isLoggedIn = false
                    xtnNavigate<MainActivity>()
                    finishAffinity()
                }
            }
            /* toolbar.isVisibleBackButton = true
             toolbar.btnBack.setOnClickListener {
                // onBackPressed()
                 onBackPressedDispatcher.onBackPressed()
             }*/
        }
    }


    private fun handleObservers() {

        viewModel.responseAddJob.observe(this) {
            if (it.isNotEmpty()) {
                // binding.progressBar.isVisible = false
                //5 is for save only
                this.infoDialogSaveReport(it, 5) { b ->
                    if (!b) {
                        AppPref.isAnswerSelected = false
                        onBackPressed()
                    }
                }
            }
        }
    }
}