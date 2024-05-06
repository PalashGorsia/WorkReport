package com.app.workreport.common


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.app.workreport.databinding.LaySelectImageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class OpenCameraFragment(
    private val block: (uri: String, bitmap: Bitmap?) -> Unit
) : DialogFragment() {
    private lateinit var binding: LaySelectImageBinding
    private var bitmapPath: Bitmap? = null

    companion object {
        const val TAG = "OpenCameraFragment"
        private const val PERMISSION_REQUEST_CODE_CAMERA = 100
        private const val PERMISSION_REQUEST_CODE_STORAGE = 200
        fun newInstance(
            block: (uri: String, bitmap: Bitmap?) -> Unit
        ): OpenCameraFragment {
            return OpenCameraFragment(block)
        }
    }

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = LaySelectImageBinding.inflate(inflater)
            handleView()
        }
        binding.lifecycleOwner = this
        return binding.root//inflater.inflate(R.layout.lay_filter, container, false)
    }

    private fun handleView() {
        binding.apply {
            layImage.isVisible = false
        }
          openCamera()
      //  showImageSourceDialog()
    }

    private fun openCamera() {
        if (haveCameraPermission()) {
            //dispatchTakePictureIntent()
            takePhoto.launch()
        } else {
            requestCameraPermission()
        }

    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }


    private fun haveCameraPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && haveStoragePermission()
        } else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && haveStoragePermission()
        }


    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE_CAMERA
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE_CAMERA
            )
        }

    }

    private fun haveStoragePermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * asking only WRITE_EXTERNAL_STORAGE as this is higher level permission include READ_EXTERNAL_STORAGE as well
     */

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
                        // check whether storage permission granted or not.
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            deniedCount++
                        }
                    }

                    if (deniedCount == 0) {
                        takePhoto.launch()
                        //dispatchTakePictureIntent()
                    } else {
                        requestCameraPermission()
                    }
                }
        }
    }

    private val takePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            try {
                val fileName = "temp_file_" + System.currentTimeMillis() + ".png"
                val isSavedSuccessfully = savePhotoToInternalStorage(fileName, it!!)
                if (isSavedSuccessfully) {
                    lifecycleScope.launch {
                        loadPhotosFromInternalStorage(fileName)
                    }
                }
            } catch (e: Exception) {
                //  block(ERROR_CODE.toString(),null)
                dismiss()
                // xtnLog("Exception takePhoto ${e.message}")
            }

        }

    //  @RequiresApi(Build.VERSION_CODES.R)
    private fun savePhotoToInternalStorage(filename: String, bmp: Bitmap): Boolean {

        return try {

            requireActivity().openFileOutput(filename, AppCompatActivity.MODE_PRIVATE).apply {
                //  val  bmpi = getResizedBitmap(bmp,2000,2000)!!
                bitmapPath = bmp
                if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, this)) {
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun loadPhotosFromInternalStorage(fileName: String) {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
//            Log.e(TAG, "file name : ${files[0].absolutePath}" )
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(fileName) }?.map {
                /*if you want to save image in bitmap
                 val bytes = it.readBytes()
                 val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                 */
                block(it.absolutePath, bitmapPath!!)
                dismiss()
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Take Photo
                    if (haveCameraPermission()) {
                        takePhoto.launch()
                    } else {
                        requestCameraPermission()
                    }
                }
                1 -> {
                    // Choose from Gallery
                    if (haveStoragePermission()) {
                        pickImageFromGallery.launch("image/*")
                    } else {
                        requestStoragePermission()
                    }
                }
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private val pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // Handle the selected image from the gallery
            uri?.let {
//                val imagePath = getImagePathFromUri(uri)
//                if (imagePath != null) {
//                    processSelectedImage(imagePath)
//                }

                try {
                    val getBitmap = uriToBitmap(requireContext(), it)
                    val fileName = "temp_file_" + System.currentTimeMillis() + ".png"
                    val isSavedSuccessfully =
                        getBitmap?.let { it1 -> savePhotoToInternalStorage(fileName, it1) }
                    if (isSavedSuccessfully == true) {
                        lifecycleScope.launch {
                            loadPhotosFromInternalStorage(fileName)
                        }
                    }
                } catch (e: Exception) {
                    //  block(ERROR_CODE.toString(),null)
                    dismiss()
                    // xtnLog("Exception takePhoto ${e.message}")
                }
            }
        }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE_STORAGE
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE_STORAGE
            )
        }

    }

    private fun processSelectedImage(imagePath: String) {
        // Process the selected image and call the provided block function
        // You can use the imagePath and then dismiss the dialog
        block(imagePath, bitmapPath)
        dismiss()
    }

    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inSampleSize = 1 // You can adjust the sample size if needed

            val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}