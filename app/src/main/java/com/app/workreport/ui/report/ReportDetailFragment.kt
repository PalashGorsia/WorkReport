package com.app.workreport.ui.report

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.workreport.databinding.FragmentPersonalHealthBinding
import com.app.workreport.model.Content
import com.app.workreport.common.DisplayImageActivity
import com.app.workreport.model.Data2
import com.app.workreport.ui.report.adapter.PersonalHealthCompleteAdapter
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.google.gson.Gson as Gson1

@AndroidEntryPoint
class ReportDetailFragment :Fragment() {
    private var adapterComplete: PersonalHealthCompleteAdapter? = null
    private lateinit var binding: FragmentPersonalHealthBinding
    private var array: List<Content> = arrayListOf()
    private var inspectionData: Data2? = null
  //  private var inspectionData: Data? = null
    private var pos: Int = 0
    private var type: String = ""
    private var isCompleted: Boolean = false
    var path: ((String) -> Unit)? = null

    companion object {
        @JvmStatic
        fun newInstance(pos: Int, type: String, complete: Boolean?) =
            ReportDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(POSITION, pos)
                    putString(TYPE, type)
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
        handleObservers()

        return binding.root
    }

    private fun handleObservers() {

    }


    private fun handleView() {
        binding.btSubmit.visibility = View.GONE
        //  (activity as BaseActivity<*, *>).showProgress(viewModel)
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
        }
    }

    private fun getDataFromDataBase() {

        binding.btSubmit.isVisible = false
        lifecycleScope.launch {
            inspectionData = Gson1().fromJson(type, Data2::class.java)
            inspectionData?.let {
                array = it.tabs[pos].content
                adapterComplete = PersonalHealthCompleteAdapter(
                    requireActivity()
                ) { entry, _, _, type, _, imagePath ->
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
                        }
                        2 -> {
                            path = imagePath
                             deleteImage(entry?.image_url?:"")
                        }
                        4 -> {
                            //  additionalComments(1,comment)
                        }
                    }
                }
                binding.recyclerLayout.recyclerView.adapter = adapterComplete
            }
            adapterComplete?.swapList(inspectionData!!, pos)
        }



    }
  private  fun deleteImage(url: String) {
        val bundle = Bundle()
        bundle.putString(IMAGE, url)
        bundle.putBoolean(IS_UPLOAD_IMAGE, true)
        bundle.putBoolean(STATUS, false)
        val intent = Intent(activity, DisplayImageActivity::class.java)
        intent.putExtra(BUNDLE, bundle)
        deleteImageResult.launch(intent)


    }




    private val deleteImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
/*
        if (result.resultCode == Activity.RESULT_OK) {
            path?.let { it("10") }
        }
*/
    }

}