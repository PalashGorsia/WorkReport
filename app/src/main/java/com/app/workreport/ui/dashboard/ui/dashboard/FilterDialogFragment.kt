package com.app.workreport.ui.dashboard.ui.dashboard


import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.app.workreport.R
import com.app.workreport.databinding.LayFilterBinding
import kotlinx.coroutines.delay

class FilterDialogFragment(
    private val filterType: Int,
    private val block: (type: Int) -> Unit
) : DialogFragment(), View.OnClickListener {
    private lateinit var binding: LayFilterBinding
    companion object {
       const val TAG = "FilterDialogFragment"
        fun newInstance(
            filterType: Int,
            block: (type: Int) -> Unit
        ): FilterDialogFragment {
            return FilterDialogFragment(filterType,block)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.text_inprogress -> {
                if (binding.isSelectProgress==true){
                    removeFilter(binding)
                    block(2)
                    dismiss()
                }else{
                    lifecycleScope.launchWhenCreated {
                        selectInProgress(binding)
                        delay(600)
                        block(0)
                        dismiss()
                    }

                }
            }
            R.id.text_complete -> {

                if (binding.isSelectComplete==true){
                    removeFilter(binding)
                    block(2)
                    dismiss()
                }else{
                    selectComplete(binding)
                    lifecycleScope.launchWhenCreated {
                        delay(600)
                        block(1)
                        dismiss()
                    }

                }


             //   block(2)
             //   this.dismiss()
            }
        }
    }

    private fun removeFilter(binding: LayFilterBinding) {


    }


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = LayFilterBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner =this
        return binding.root//inflater.inflate(R.layout.lay_filter, container, false)
    }

    private fun handleView() {
        binding.apply {
            if (filterType==0){
                selectInProgress(binding)

            }else if (filterType==1){
                selectComplete(binding)
            }else{
                removeFilter(binding)
            }
            textInprogress.setOnClickListener(this@FilterDialogFragment)
            textComplete.setOnClickListener(this@FilterDialogFragment)
        }
    }

    private fun selectComplete(binding: LayFilterBinding) {
        binding.isSelectComplete =true
        binding.isSelectProgress = false
    }

    private fun selectInProgress(binding: LayFilterBinding) {
        binding.isSelectComplete =false
        binding.isSelectProgress = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
) {
//    requireDialog().window?.setWindowAnimations(
//        R.style.DialogAnimation
//    )
}
}