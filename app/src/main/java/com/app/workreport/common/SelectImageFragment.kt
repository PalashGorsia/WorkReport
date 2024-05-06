package com.app.workreport.common


import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.app.workreport.R
import com.app.workreport.databinding.LaySelectImageBinding
import com.app.workreport.util.ERROR_CODE
import com.app.workreport.util.xtnLog

class SelectImageFragment(
    private val imagePath: String,
    private var imageCount: Int,
    private val block: (type: Int) -> Unit
) : DialogFragment(), View.OnClickListener {
    private lateinit var binding: LaySelectImageBinding
    companion object {
       const val TAG = "SelectImageFragment"
        fun newInstance(imagePath:String,bmp:Bitmap?,imageCount:Int,
            block: (type:Int) -> Unit
        ): SelectImageFragment {
            return SelectImageFragment(imagePath, imageCount, block)
        }
    }

    override fun onClick(v: View?) {
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
    }


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()){
            binding = LaySelectImageBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner =this
        return binding.root//inflater.inflate(R.layout.lay_filter, container, false)
    }

    private fun handleView() {
       // openCamera()
        binding.apply {
            if (imagePath.equals(ERROR_CODE.toString(),true)){
                btnSelect.setTextColor(resources.getColor(R.color.text_gray))
                btnReserve.setTextColor(resources.getColor(R.color.text_gray))
                btnDiscard.setOnClickListener(this@SelectImageFragment)
            }else{
                imageView.setImageURI(Uri.parse(imagePath))
                //  imageView.setImageBitmap(bitmap)
                btnSelect.setOnClickListener(this@SelectImageFragment)
                btnDiscard.setOnClickListener(this@SelectImageFragment)
                xtnLog("Image Count Select Image $imageCount")
                if (imageCount==2) imageCount =0
                if (imageCount < 1)
                    btnReserve.setOnClickListener(this@SelectImageFragment)
                else
                    btnReserve.setTextColor(resources.getColor(R.color.text_gray))
            }
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog?.setCanceledOnTouchOutside(false)
    }



}