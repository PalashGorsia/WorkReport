package com.app.workreport.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.app.workreport.databinding.ActivityDisplayImageBinding
import com.app.workreport.ui.checklist.CheckListViewModel
import com.app.workreport.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DisplayImageActivity : AppCompatActivity() {

    private var image: String? = null
    private var isUploadImage: Boolean = false
    private var showDelete: Boolean = true
    private lateinit var binding: ActivityDisplayImageBinding
    private val viewModel: CheckListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  initToolbarImage(binding.toolbarLayout)

        intent.getBundleExtra(BUNDLE)?.let {
            image = it.getString(IMAGE)
            //     Log.e("TAG", "ActivityDisplayImageBinding: $image")
//            Log.e(
//                "TAG",
//                "Path Image: ${FileUriUtils.getRealPath(this, image!!.toUri())}}"
//            )
            isUploadImage = it.getBoolean(IS_UPLOAD_IMAGE)
            showDelete = it.getBoolean(STATUS)

        }

        binding.toolbarLayout.ivDelete.isVisible = showDelete
        binding.btSubmit.isVisible = showDelete
        // binding.noImage.isVisible=!showDelete

        binding.noImage.isVisible = image.isNullOrEmpty()


        if (image!!.contains("http")) {
            loadImage(binding.imageView, image ?: "")
        } else {
            binding.imageView.setImageURI(image?.toUri())

        }

        binding.btSubmit.setOnClickListener {
            var filePath: String? = null
            image?.let {
                filePath = compressImageCheckList(this, it)!!
            }
            binding.progressBar.isVisible = true
            viewModel.uploadImage(filePath!!)
        }

        binding.toolbarLayout.ivDelete.setOnClickListener {

            val intent = Intent()
            intent.putExtra("url", "")
            setResult(RESULT_OK, intent)
            finish()

        }
        viewModel.uploadImageData.observe(this) {
            binding.progressBar.isVisible = false
            val intent = Intent()
            intent.putExtra(URL, it.file)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    fun getRealPathFromUri(uri: Uri?): String? {
        var realPath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        contentResolver.query(uri!!, projection, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                realPath = cursor.getString(columnIndex)
            }
        }
        return realPath
    }

}
