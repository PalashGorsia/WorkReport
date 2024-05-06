package com.app.workreport.common


import android.os.Bundle
import android.view.*
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.app.workreport.R
import com.app.workreport.databinding.LayViewImageBinding
import com.app.workreport.util.loadImage

class ViewImageFragment(private val imagePath: String,
                        private val block: ( type: Int) -> Unit
) : DialogFragment(), View.OnClickListener {
    private lateinit var binding: LayViewImageBinding
    companion object {
       const val TAG = "SelectImageFragment"
        fun newInstance(imagePath:String,
            block: (type:Int) -> Unit
        ): ViewImageFragment {
            return ViewImageFragment(imagePath,block)
        }
    }

    override fun onClick(v: View?) {
/*
        when (v?.id) {
            R.id.btnSelect -> {
                block(0)
                dismiss()
            }
            R.id.btnDiscard -> {
                block(1)
                dismiss()

            }
            R.id.btnReserve ->{
                block(2)
                dismiss()
            }
        }
*/
    }


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = LayViewImageBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner =this
        return binding.root//inflater.inflate(R.layout.lay_filter, container, false)
    }

    private fun handleView() {
        binding.apply {
            requireActivity().loadImage(imageView,imagePath)

            closeDialog.setOnClickListener {
                dismiss()
            }
           // imageView.setImageURI(Uri.parse(imagePath))

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialogStyle)
    }


}