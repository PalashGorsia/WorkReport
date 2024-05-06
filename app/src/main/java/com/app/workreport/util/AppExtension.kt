package com.app.workreport.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.os.*
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.CheckResult
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.app.workreport.R
import com.app.workreport.model.Data
import com.app.workreport.ui.report.ReportSentDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Context.xtnToast(message: String = "") {
    // Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun xtnLog(message: String = "", tag: String = TAGE) {
    // Log.e(tag, message )
}

inline val APP_PREFERENCE: String
    get() = "app_preferences"

/**
 * @author Ravindra Singh
 * Extension method to run block of code after specific Delay.
 */
fun xtnRunDelayed(action: () -> Unit, delay: Long, timeUnit: TimeUnit = TimeUnit.MILLISECONDS) {
    Handler(Looper.getMainLooper()).postDelayed(action, timeUnit.toMillis(delay))
}

/**
 * @author Ravindra Singh
 * Extension method for show message SnackBar
 * */
fun View.showSnackBar(message: String) {
    try {
        val snackbar: Snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * @author Ravindra Singh
 * Extension method for navigating to other activity with/without Data
 * */
inline fun <reified T : Activity> Context.xtnNavigate(bundle: Bundle? = null) =
    startActivity(Intent(this, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
    })

inline fun <reified T : Activity> Context.xtnNavigate2(bundle: Bundle? = null) =
    startActivity(Intent(this, T::class.java).apply {
        if (bundle != null) putExtras(bundle)
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    })


/**
 * @author Ravindra Singh
 * Extension method for hide key board
 */
fun View.hideKeyboard() {
    val imm =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


/*fun getDateTimeAsFormattedString(dateAsLongInMs: Long,dateFormat:String): String? {
   try {
       return SimpleDateFormat(dateFormat).format(Date(dateAsLongInMs))
   } catch (e: Exception) {
       return null // parsing exception
   }
}*/

/**
 * @author Ravindra Singh
 * Method for saving a value to SharedPreference.
 * Note All Shared preference are in String type
 * */
fun Context.xtnPutKey(key: String, value: String = "") {
    getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
        .apply {
            this.edit()
                .putString(key, value)
                .apply()
        }
}

/**
 * @author Ravindra Singh
 * Method for getting value from SharedPrefrences
 * @return value or null
 * */

fun Context.xtnGetKey(key: String): String? {
    return getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE).getString(key, null)
}
/*
* for select date */


/**
 * Check internet connection
 *
 * @param context = context
 * @return
 */
fun Context.isNetworkAvailable(): Boolean {
    val cm =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnectedOrConnecting
}


fun Context?.logOutDialog(message: String = "", onLogOut: () -> Unit) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_logout)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    val dialogBtnNo = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonNo)
    val icon = dialog?.findViewById<AppCompatImageView>(R.id.icon)
    if (message.isNotEmpty()) {
        dialog?.findViewById<TextView>(R.id.message)?.text = message
        //dialogBtnYes?.text = this?.resources?.getString(R.string.accept)
        //dialogBtnNo?.text = this?.resources?.getString(R.string.reject)
        icon?.setImageDrawable(this?.resources?.getDrawable(R.drawable.logo))
    }


    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        onLogOut.invoke()
    }
    dialogBtnNo?.setOnClickListener {
        dialog.dismiss()
    }
    dialog?.show()
}


fun Context?.saveChangesAlertDialog(message: String = "", onLogOut: (status: String) -> Unit) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_logout)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    val dialogBtnNo = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonNo)
    val icon = dialog?.findViewById<AppCompatImageView>(R.id.icon)
    if (message.isNotEmpty()) {
        dialog?.findViewById<TextView>(R.id.message)?.text = message
        //dialogBtnYes?.text = this?.resources?.getString(R.string.accept)
        //dialogBtnNo?.text = this?.resources?.getString(R.string.reject)
        icon?.setImageDrawable(this?.resources?.getDrawable(R.drawable.logo))
    }


    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        onLogOut.invoke(this?.getString(R.string.yes) ?: "")
    }
    dialogBtnNo?.setOnClickListener {
        dialog.dismiss()
        onLogOut.invoke(this?.getString(R.string.no) ?: "")
    }
    dialog?.show()
}

fun Context?.invitationDialog(
    message: String = "",
    buttonText: String = "",
    updateStatus: (status: Int) -> Unit
) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_ivitation)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    val dialogBtnNo = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonNo)
    val icon = dialog?.findViewById<AppCompatImageView>(R.id.icon)
    val closeDialog = dialog?.findViewById<AppCompatImageView>(R.id.closeDialog)
    if (message.isNotEmpty()) {
        dialog?.findViewById<TextView>(R.id.message)?.text = message
        dialogBtnYes?.text = this?.resources?.getString(R.string.accept)
        dialogBtnNo?.text = this?.resources?.getString(R.string.reject)
        icon?.setImageDrawable(this?.resources?.getDrawable(R.drawable.logo))
    }

    if (buttonText.isNotEmpty()) {
        dialogBtnNo?.visibility = View.GONE
        dialogBtnYes?.text = this?.resources?.getString(R.string.ok)
    }


    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        updateStatus(2)
    }
    closeDialog?.setOnClickListener {
        dialog.dismiss()
    }
    dialogBtnNo?.setOnClickListener {
        dialog.dismiss()
        updateStatus(3)
    }
    dialog?.show()
}

fun Context?.infoDialog(onLogOut: () -> Unit) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_save_image)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        onLogOut.invoke()
    }

    dialog?.show()
}

fun Context?.infoDialogError(type: Int, message: String = "", onLogOut: () -> Unit) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_info_sever_error)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    val textInfo = dialog?.findViewById<TextView>(R.id.textInfo)
    if (type == 1) {
        textInfo?.setText(R.string.lost_internet)
    } else if (type == 3) {
        textInfo?.setText(R.string.please_wait_images_are_not_uploaded_to_sever)
    } else if (type == 4) {
        textInfo?.text = message
    } else {
        textInfo?.setText(R.string.lost_sever_connection)
    }
    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        onLogOut.invoke()
    }

    dialog?.show()
}

fun Context?.infoDialogSaveReport(
    message: String = "",
    reportType: Int,
    onLogOut: (status: Boolean) -> Unit
) {
    val dialog = this?.let { Dialog(it) }
    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.setCancelable(false)
    dialog?.setContentView(R.layout.dialog_save_report)
    dialog?.window?.setLayout(
        LinearLayoutCompat.LayoutParams.MATCH_PARENT,
        LinearLayoutCompat.LayoutParams.WRAP_CONTENT
    )
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val dialogBtnYes = dialog?.findViewById<AppCompatButton>(R.id.appCompatButtonYes)
    val icon = dialog?.findViewById<AppCompatImageView>(R.id.imageView)
    val textSaveReport = dialog?.findViewById<TextView>(R.id.reportSave)
    val textInfoReport = dialog?.findViewById<TextView>(R.id.infoText)

    if (reportType == 1) {
        textSaveReport?.setText(R.string.report_sent)
        textInfoReport?.setText(R.string.your_report_is_saved_to_admin_for_review)
    } else if (reportType == 2) {
        textSaveReport?.setText(R.string.report_save)
        textInfoReport?.setText(R.string.your_report_is_saved_to_admin_for_review)
    } else if (reportType == 4) {
        textSaveReport?.setText(R.string.submitted)
        textInfoReport?.text = message
        this?.resources?.getColor(R.color.green)?.let { icon?.setColorFilter(it) }
        // icon?.backgroundTintList =
        icon?.setImageDrawable(this?.resources?.getDrawable(R.drawable.ic_report))
    } else if (reportType == 5) {
        textSaveReport?.setText(R.string.saved)
        textInfoReport?.text = message
        this?.resources?.getColor(R.color.green)?.let { icon?.setColorFilter(it) }
        // icon?.backgroundTintList =
        icon?.setImageDrawable(this?.resources?.getDrawable(R.drawable.ic_report))

    }
    dialogBtnYes?.setOnClickListener {
        dialog.dismiss()
        when (reportType) {
            1 -> onLogOut.invoke(true)
            4 -> onLogOut.invoke(true)
            5 -> onLogOut.invoke(false)
            else -> onLogOut.invoke(false)
        }
    }
    dialog?.show()
}


fun String.xtnPartFromString(): RequestBody {
    return this.toRequestBody(MultipartBody.FORM)
}

fun String.xtnAsRequestBodyFromPath(
    mimeType: String = "image/*",
    paramName: String
): MultipartBody.Part {
    val file = File(this)
    return MultipartBody.Part.createFormData(
        paramName, file.name, file.asRequestBody(mimeType.toMediaTypeOrNull())
    )
}

fun String.xtnIsURL(): Boolean {
    return (this.isEmpty().not()) && (
            this.startsWith("HTTP", true) ||
                    this.startsWith("HTTPS", true) ||
                    this.startsWith("FTP", true))
}

fun String.xtnHandleError(): String {

    val stringBuilder = StringBuilder()
    stringBuilder.append("")
    this.let {
        try {
            stringBuilder.append(JSONObject(it).getString("message"))
            return stringBuilder.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
            return ""
        }
    }
}

@SuppressLint("ResourceType")
fun Context.loadImage(view: ImageView, url: String) {
    if (url.isNotEmpty()) {
        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.apply {
            strokeWidth = 5f
            centerRadius = 30f
            setColorSchemeColors(
                this@loadImage.resources.getColor(R.color.green)
                // getColor(Color.GREEN)
            )
            start()
        }
        if (url.contains("http")) {
            Picasso.get()
                .load(url)
                .placeholder(circularProgressDrawable)
                .into(view)
        } else {
            val file = File(url)
            Picasso.get()
                .load(file)
                .placeholder(circularProgressDrawable)
                .into(view)
        }
    } else return

}

fun Context.xtnSetLanguage(type: String) {
    val locale = Locale(type)
    val res = this.resources
    val dm = res.displayMetrics
    val conf = res.configuration
    conf.locale = locale
    res.updateConfiguration(conf, dm)
}

/*create by Ravindra
Avoid button multiple rapid clicks */
fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
/*
* create by Ravindra
* for OnTextChange*/

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChange(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun Context.shootingType(shootingName: String): Int {
    return when (shootingName) {
        resources.getString(R.string.text_before) -> 1
        resources.getString(R.string.text_after) -> 2
        resources.getString(R.string.at_work) -> 3
        else -> 0
    }
}


fun getRandomString(length: Int): String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}

fun setImageInText(context: Context, tex: String, textView: TextView) {
    val text = "$tex T"
    val ssb = SpannableStringBuilder("$text")
    val icon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_info)!!
    icon.setBounds(5, 5, icon.intrinsicWidth, icon.intrinsicHeight)
    val image = ImageSpan(icon, ImageSpan.ALIGN_BASELINE)
    ssb.setSpan(
        image,
        text.length - 1, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )
    textView.setText(ssb, TextView.BufferType.SPANNABLE)
}

fun openFragmentReplace(
    fm: FragmentManager,
    id: Int, fragment: Fragment, tag: String, addToBack: Boolean
) {
    when (addToBack) {
        true -> fm
            .beginTransaction()
            .replace(id, fragment, tag)
            .addToBackStack(null)
            .commit()

        false -> fm
            .beginTransaction()
            .replace(id, fragment, tag)
            .commit()
    }
}


fun compressImageCheckList(context: Context, path: String): String? {
    val filePath =
        FileUriUtils.getRealPath(context, path.toUri()) //getRealPathFromURI(context, imageUri)
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true
    var bmp = BitmapFactory.decodeFile(filePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
    val maxHeight = 816.0f
    val maxWidth = 612.0f
    var imgRatio = actualWidth / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = maxHeight / actualHeight
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = maxWidth / actualWidth
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
//          load the bitmap from its path
        bmp = BitmapFactory.decodeFile(filePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
        return null
    }
    try {

        if (actualWidth <= 0)
            actualWidth = 120
        if (actualHeight <= 0)
            actualHeight = 120
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
        return null
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap!!)
    canvas.setMatrix(scaleMatrix)
    bmp?.let {
        canvas.drawBitmap(
            it,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )
    }


//      check the rotation of the image and display it properly
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath ?: "")
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        Log.d("EXIF", "Exif: $orientation")
        val matrix = Matrix()
        if (orientation == 6) {
            matrix.postRotate(90f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 3) {
            matrix.postRotate(180f)
            Log.d("EXIF", "Exif: $orientation")
        } else if (orientation == 8) {
            matrix.postRotate(270f)
            Log.d("EXIF", "Exif: $orientation")
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    var out: FileOutputStream? = null
    val filename = getFilename(context)
    try {
        out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
        scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return filename
}


fun getFilename(context: Context): String {

    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File(storageDir, "images")
    if (!file.exists()) {
        file.mkdirs()
    }
    return file.absolutePath.toString() + "/" + System.currentTimeMillis() + ".jpg"
}


fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio =
            Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = width * height.toFloat()
    val totalReqPixelsCap = reqWidth * reqHeight * 2.toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}


fun allImportantItemsChecked(answerData: String): Boolean {
    var isAllImportantMarked = true
    val inspectionData = Gson().fromJson(answerData, Data::class.java)
    inspectionData!!.tabs.forEach {
        it.content.forEach { contentItem ->
            contentItem.todos.forEach { todoItem ->
                if (todoItem.is_important) {
                    if (todoItem.NC == "0" &&
                        todoItem.NG == "0" &&
                        todoItem.O == "0"
                    ) {
                        val da = it.title
                        xtnLog(da)
                        isAllImportantMarked = false
                    }
                }
            }
        }
    }
    return isAllImportantMarked
}

fun showAlert(
    supportFragmentManager: FragmentManager,
    title: String, message: String,
    showCancel: Boolean, buttonText: String, callback: (status: Boolean) -> Unit
) {
    val alert = ReportSentDialogFragment
        .newInstance(
            title,
            message,
            showCancel,
            buttonText
        ) {
            callback(it)
        }
    alert.show(supportFragmentManager, "")
}

/**
 * @author Satnam Singh
 * Toggle view's visibility to VISIBLE AND GONE on conditions
 **/
fun View.xtnToggleShowIf(predicate: () -> Boolean) {
    visibility = if (predicate()) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun getLocale(): String {
    val languageCode = Locale.getDefault().language.toUpperCase()
    return if (languageCode == LANGUAGE_KEY_JAPANESE.toUpperCase()) LANGUAGE_KEY_WEB_JAPANESE
    else LANGUAGE_KEY_WEB_ENGLISH
}


fun getDay(
    date: String?,
    currentFormat: String?
): String {
    if (date == null) return ""
    val finalDate: String
    val curFormater =
        SimpleDateFormat(currentFormat!!, Locale.US)
    var dateObj: Date? = null
    try {
        dateObj = curFormater.parse(date)
    } catch (e: ParseException) {
        // Logger.exceptionLog(e)
    }
    val postFormater: SimpleDateFormat = SimpleDateFormat(EE, Locale.US)
    finalDate = if (dateObj != null) postFormater.format(dateObj) else ""
    return finalDate
}


fun main() {
    val data = "https://www.smastep.com/?workPlanId=642bcc43d58ccb53aefdccf3"
    if (data.contains("workPlanId") && data.contains("jobNumber")) {
        val workPlanId = data.split("workPlanId=")[1].split("&")[0]
        val jobNo = data.split("jobNumber=")[1]

        println("work = $workPlanId  Job = $jobNo")
    }
    //   val result = Ase.decrypt("wlrDRm5tc17dtLtWk/0cKq7Nw+RPkpgHnLKhWyoGwfM=")
    val day = convertDateFormat("2023-01-03", YYYY_MM_DD, EE)
    println(day)
}


