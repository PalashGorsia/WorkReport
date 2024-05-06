package com.app.workreport.ui.checklist

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.app.workreport.R
import com.app.workreport.common.DisplayImageActivity
import com.app.workreport.data.model.CheckListTable
import com.app.workreport.data.model.QuestionData
import com.app.workreport.databinding.FragmentPersonalHealthBinding
import com.app.workreport.model.AdditionalInfo
import com.app.workreport.model.AnswerData
import com.app.workreport.model.AnswerData2
import com.app.workreport.model.AnswerDataWithoutAdditionaInfo
import com.app.workreport.model.Content
import com.app.workreport.model.Data
import com.app.workreport.ui.checklist.adapter.PersonalHealthAdapter
import com.app.workreport.ui.checklist.dialog.FinalCommentsFragment
import com.app.workreport.util.AppPref
import com.app.workreport.util.BUNDLE
import com.app.workreport.util.IMAGE
import com.app.workreport.util.IS_UPLOAD_IMAGE
import com.app.workreport.util.PERMISSION_REQUEST_CODE_CAMERA
import com.app.workreport.util.POSITION
import com.app.workreport.util.STATUS
import com.app.workreport.util.SimpleDividerItemDecoration
import com.app.workreport.util.TYPE
import com.app.workreport.util.allImportantItemsChecked
import com.app.workreport.util.currentDateFormat
import com.app.workreport.util.getLocale
import com.app.workreport.util.infoDialogSaveReport
import com.app.workreport.util.showAlert
import com.app.workreport.util.showSnackBar
import com.app.workreport.util.xtnLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class PersonalHealthFragment(private val viewPager: ViewPager2, private val jobId: String) :
    Fragment() {
    // private var adapter: PersonalHealthAdapter? = null
    private var adapter: PersonalHealthAdapter? = null

    //  private var adapter: PersonalHealthInProgressAdapter? = null
    private var array: List<Content> = arrayListOf()
    private var inspectionData: Data? = null
    private var answerData: AnswerData? = null

    private val viewModel: CheckListViewModel by viewModels()
    private lateinit var binding: FragmentPersonalHealthBinding
    private var pos: Int = 0
    private var isTeamLeadOrWorker: Int = 0    //1=worker 2=teamLead
    private var type: String = ""
    private var imagePath: String = ""
    private var isReportSubmitted: Boolean = false

    var capturedImageTodoIndex: Int = 0
    var isSavedOrSubmitted: Int = 0 //4 = submitted 5=saved
    var path: ((String) -> Unit)? = null


    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val REQUEST_PICK_IMAGE = 102
        var answerList: ArrayList<QuestionData> = arrayListOf()

        fun newInstance(pos: Int, type: String, viewPager: ViewPager2, jobId: String): Fragment {
            return try {
                PersonalHealthFragment(viewPager, jobId).apply {
                    arguments = Bundle().apply {
                        putInt(POSITION, pos)
                        putString(TYPE, type)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Fragment()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentPersonalHealthBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner = this
        if (AppPref.leadID == AppPref.userID) {
            binding.btSubmit.tag = "Submit"
            binding.btSubmit.text = getString(R.string.submit)
            isSavedOrSubmitted = 4
            isTeamLeadOrWorker = 2
        } else {
            binding.btSubmit.tag = "Save"
            binding.btSubmit.text = getString(R.string.save)
            isTeamLeadOrWorker = 1
            isSavedOrSubmitted = 5

        }
        handleObservers()

        return binding.root

    }

    private fun handleObservers() {

        viewModel.responseAddJob.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.progressBar.isVisible = false
                requireActivity().infoDialogSaveReport(it, isSavedOrSubmitted) { b ->
                    if (b) {
                        AppPref.isAnswerSelected = false
                        requireActivity().onBackPressed()
                    } else {
                        viewModel.getJobCheckList()
                    }
                }
            }
        }


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
                                jobId,
                                AppPref.employeeId ?: ""
                            )
                        )

                    }
                }
                //userId = JobId
            }
        }

    }


    private fun handleView() {
        val layoutManager = LinearLayoutManager(requireContext())
        requireArguments().let {
            pos = it.getInt(POSITION)
            type = it.getString(TYPE, "")
        }
        getDataFromDataBase()
        binding.apply {
            recyclerLayout.recyclerView.layoutManager = layoutManager
            recyclerLayout.recyclerView.addItemDecoration(
                SimpleDividerItemDecoration(requireContext())
            )

            btSubmit.setOnClickListener {
                lifecycleScope.launch {
                    val lis = viewModel.getCheckListBasedOnJob()
                    val dbString = lis.json
                    inspectionData = Gson().fromJson(dbString, Data::class.java)
                    Log.e("TAG", "inspectionData: ${inspectionData}")
                    if (binding.btSubmit.tag == "Submit") {
                        if (allImportantItemsChecked(dbString)) {
                            showAdditionalCommentsPopup()
                        } else {
                            showDialog()
                        }
                    } else {
                        val answerList = viewModel.getAnswerData()
                        val answerData2 = AnswerData2(answerList)
                        Log.e("TAG", "answerData2: $answerData2")
                        val answerList2ToJson = Gson().toJson(answerData2)
                        Log.e("TAG", "answerList2ToJson: $answerList2ToJson")
                        val additionInfoToJson = Gson().toJson(AdditionalInfo("", ""))
                        var result: Any? = null
                        if (binding.btSubmit.tag == "Save") {
                            AppPref.isAnswerSelected = false
                            result = AnswerDataWithoutAdditionaInfo(
                                answerList2ToJson,
                                "string",
                                AppPref.jobId ?: "",
                                getLocale(),
                                AppPref.employeeId ?: "",
                                isTeamLeadOrWorker
                            )
                        } else {
                            result = AnswerData(
                                additionInfoToJson,
                                answerList2ToJson,
                                "string",
                                AppPref.jobId ?: "",
                                getLocale(),
                                AppPref.employeeId ?: "",
                                isTeamLeadOrWorker
                            )

                        }
                        viewModel.addCheckListNew(result)

                    }

                }
            }

            btSave.setOnClickListener {
                lifecycleScope.launch {
                    isSavedOrSubmitted = 5
                    AppPref.isAnswerSelected = false
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
                    Log.e("TAG", "save button result without selecting anything: $result")

                }

            }
        }


    }


    private fun getDataFromDataBase() {
        /*in progress report*/

        /*in progress data yesterday date */
        binding.btSubmit.isVisible = false
        lifecycleScope.launch {
            inspectionData = Gson().fromJson(type, Data::class.java)
            // inspectionData = Gson().fromJson(type, Data2::class.java)
            inspectionData?.let {
                // xtnLog("getCheckListBasedOnDate 123")


                array = it.tabs[pos].content
                adapter = PersonalHealthAdapter(
                    requireActivity(), viewModel
                ) { entry, pos, posChild, type, comment, imagePath ->
                    //0 save data database
                    //1 take picture
                    //2 delete picture
                    when (type) {
                        0 -> {
                            path = imagePath
                            imagePath("ert")
                        }

                        1 -> {
                            path = imagePath
                            captureImage(posChild, pos)
                            /// path = imagePath
                        }

                        2 -> {
                            path = imagePath
                            deleteImage(entry?.image_url ?: "")
                        }

                        4 -> {
                            //  additionalComments(1,comment)
                        }
                    }
                }
                binding.recyclerLayout.recyclerView.adapter = adapter

                if (isReportSubmitted) {
                    binding.btSubmit.isVisible = false
                    binding.btSave.isVisible = false

                } else {
                    if (AppPref.leadID == AppPref.userID && AppPref.isSingleWorker) {
                        binding.btSubmit.isVisible = it.tabs.size == pos + 1
                        binding.btSave.isVisible = true
                        //   binding.btSave.isVisible = it.tabs.size == pos + 1
                    } else if (AppPref.leadID != AppPref.userID && !AppPref.isSingleWorker) {
                        binding.btSubmit.isVisible = true
                        binding.btSave.isVisible = false
                    } else {
                        binding.btSave.isVisible = true
                        //   binding.btSave.isVisible = it.tabs.size == pos + 1
                        binding.btSubmit.isVisible = it.tabs.size == pos + 1


                    }


                }

            }
            adapter?.swapList(inspectionData!!, pos)

        }
    }


    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE_CAMERA
        )
    }

    private fun clickImage() {
        requireContext().checkPermission(object : PermissionHandler() {
            override fun onGranted() {
                selectImage()
            }
        })
//        OpenCameraFragment.newInstance { ImagePath, _ ->
//        val bundle = Bundle()
//        bundle.putString(IMAGE, imagePath)
//        bundle.putBoolean(STATUS, true)
//        val intent = Intent(activity, DisplayImageActivity::class.java)
//        intent.putExtra(BUNDLE, bundle)
//        startForResultImage.launch(intent)
//        }.show(childFragmentManager, OpenCameraFragment.TAG)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE_CAMERA ->
                if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.CAMERA) {
                    var deniedCount = 0
                    for (i in 1..grantResults.size) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            deniedCount++
                        }
                    }
                    if (deniedCount == 0) {
                        clickImage()
                    } else {
                        requestCameraPermission()
                    }
                }
        }
    }


//todo dialog issue

    private fun gotoNextPage(selectPo: Int) {
        try {
            val st = AppPref.jumpToNextPage
            if (st == true) {
                if (selectPo != viewPager.currentItem)
                    viewPager.setCurrentItem(selectPo, true)
                AppPref.jumpToNextPage = false
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    private fun showDialog() {
        showAlert(
            childFragmentManager,
            "",
            getString(R.string.pls_mark_all_important_item),
            false,
            getString(R.string.ok)
        ) {
            if (it) {
                lifecycleScope.launch {
                    AppPref.jumpToNextPage = true
                    val selectPo = jumpCurrentTab()
                    gotoNextPage(selectPo)
                }
            }
        }
    }


    private fun captureImage(index: Int, groupIndex: Int) {
        capturedImageTodoIndex = index
        requestCameraPermission()
    }


    private fun additionalComments(string: String) {
        lifecycleScope.launch {
            val data = string.split("@")
            val additionalInfo = AdditionalInfo(data[0], data[1])
            val lis = viewModel.getCheckListBasedOnJob()
            val dbString = lis.json
            inspectionData = Gson().fromJson(dbString, Data::class.java)
            inspectionData?.let {
                it.additionalInfo = AdditionalInfo("", "")
                //  it.additionalInfo = additionalInfo
                /*
                                 it.tabs.forEach { tab ->
                                     val score = calculateScore(tab.content)
                                     xtnLog("${tab.title} ${score}","FinalScore")
                                     tab.score = score//calculateScore(tab.content)
                                 }
                */
                it
            }
            xtnLog("Api Call")
            val answerList = viewModel.getAnswerData()
            val answerData2 = AnswerData2(answerList)
            val answerList2ToJson = Gson().toJson(answerData2)
            val additionInfoToJson = Gson().toJson(additionalInfo)

            val result = AnswerData(
                additionInfoToJson,
                answerList2ToJson,
                "string",
                AppPref.jobId ?: "",
                getLocale(),
                AppPref.employeeId ?: "",
                isTeamLeadOrWorker
            )
            callApiAddInspection(result)

        }

    }


    private suspend fun jumpCurrentTab(): Int {
        val isAllImportantMarked = 0
        val lis = viewModel.getCheckListBasedOnJob()
        val dbString = lis.json
        inspectionData = Gson().fromJson(dbString, Data::class.java)
        for (i in 0 until inspectionData?.tabs?.size!!) {
            inspectionData?.tabs!![i].content.forEach { contentItem ->
                contentItem.todos.forEach { todoItem ->
                    if (todoItem.is_important) {
                        if (todoItem.NC == "0" &&
                            todoItem.NG == "0" &&
                            todoItem.O == "0"
                        ) {
                            return i
                        }
                    }
                }

            }
        }
        return isAllImportantMarked
    }


    private fun callApiAddInspection(result: AnswerData) {
        var totalScore = 0.0f
        inspectionData?.let {
            it.tabs.forEach { tab ->
                totalScore += tab.score.toFloat()
            }
        }
        //Uplers
        //   xtnLog("Total Score $totalScore","FinalScore")

        binding.progressBar.isVisible = true
        val string = Gson().toJson(inspectionData)
        if (allImportantItemsChecked(string)) {
//            viewModel.addCheckList(
//                string
//            )
            viewModel.addCheckListNew(result)
        } else {
            binding.root.showSnackBar(
                getString(R.string.pls_mark_all_important_item)
            )
        }

    }

    //todo save Image image
    val startForResultImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            result.data?.let { mData ->
                path?.let { it(mData.getStringExtra("url").toString()) }
            }
        }
    }

    val deleteImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            path?.let { it("10") }
        }
    }


    fun deleteImage(url: String) {
        val bundle = Bundle()
        if (!url.isNullOrEmpty()) {
            bundle.putString(IMAGE, url)
            bundle.putBoolean(IS_UPLOAD_IMAGE, true)
            bundle.putBoolean(STATUS, !isReportSubmitted)
            val intent = Intent(activity, DisplayImageActivity::class.java)
            intent.putExtra(BUNDLE, bundle)
            deleteImageResult.launch(intent)
        }


    }

    private fun showAdditionalCommentsPopup() {
        isSavedOrSubmitted = 4
        val addComment = FinalCommentsFragment.newInstance(
            "",
            ""
        ) { t, comment ->
            if (t == 1)
                additionalComments(comment)
        }
        addComment.show(childFragmentManager, "")
    }


    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            getString(R.string.gallery),
            getString(R.string.camera),
            getString(R.string.cancel)
        )
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(activity)
        builder.setTitle(getString(R.string.upload_photo))
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                getString(R.string.camera) -> {
                    openCamera()
                }

                getString(R.string.gallery) -> {
                    openGallery()
                }

                getString(R.string.cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"

            startActivityForResult(intent, REQUEST_PICK_IMAGE)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val photo = data?.extras?.get("data") as Bitmap
                    imagePath = getImageUri(requireActivity(), photo).toString()
                    Log.e("1010", "REQUEST_IMAGE_CAPTURE: $imagePath")
                    val bundle = Bundle()
                    bundle.putString(IMAGE, imagePath)
                    bundle.putBoolean(STATUS, true)
                    val intent = Intent(activity, DisplayImageActivity::class.java)
                    intent.putExtra(BUNDLE, bundle)
                    //  intent.putExtra(IMAGE, imagePath)
                    //  intent.putExtra(STATUS, true)
                    startForResultImage.launch(intent)

                }

                REQUEST_PICK_IMAGE -> {
                    imagePath = data?.data.toString()
                    Log.e("1010", "REQUEST_PICK_IMAGE: $imagePath")
                    val bundle = Bundle()
                    bundle.putString(IMAGE, data?.data.toString())
                    bundle.putBoolean(STATUS, true)
                    val intent = Intent(activity, DisplayImageActivity::class.java)
                    intent.putExtra(BUNDLE, bundle)
                    //intent.putExtra(STATUS, true)
                    startForResultImage.launch(intent)
                }
            }
        }
    }

    fun getImageUri(activity: Activity, photo: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            activity.contentResolver,
            photo, "Title", null
        )
        return Uri.parse(path)
    }


    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    fun Context.checkPermission(permissionHandler: PermissionHandler) {
        val options: Permissions.Options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            this, permissions,
            "Please provide permissions",
            options, permissionHandler
        )
    }


}