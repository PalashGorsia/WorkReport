package com.app.workreport.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.app.workreport.R
import com.app.workreport.container.AppContainer
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

@BindingAdapter("bindDate")
fun bindDateDDMMYYY(textView: TextView, date: String?) {
    date.let {
        textView.text = date?.let { it1 -> convertUTCToLocal(it1) }
    }
}

@BindingAdapter("bindDateLeave")
fun bindDateLeave(textView: TextView, date: String?) {
    date.let {
        textView.text = convertDateFormat(it, YYYY_MM_DD_T_HH_MM_SS_24, DDMMYYY)
    }
}

@BindingAdapter("bindType")
fun bindType(textView: TextView, type: String?) {
    if (Locale.getDefault().language.equals(LANGUAGE_KEY_JAPANESE,true)){
        when(type){
            LEAVE ->{
                textView.text = textView.context.resources.getString(R.string.leave)
            }
            HOLIDAY ->{
                textView.text = textView.context.resources.getString(R.string.holiday)
            }
            WEEKEND ->{
                textView.text = textView.context.resources.getString(R.string.weekend)
            }
        }
    }else{
        textView.text = type?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}



@BindingAdapter("bindImage")
fun bindImage(imageView: ImageView, url: String?) {
    url?.let {
        if (it.isNotEmpty())
            xloadImages(imageView, url, R.drawable.ic_select_image)
        else
            AppContainer.INSTANCE.picasso.load(R.drawable.ic_select_image).into(imageView)
        //  xtnLoadImage(any = it, imageView = imageView)
    }
}

@BindingAdapter("loadImage", "placeholder", requireAll = false)
fun xloadImages(
    view: ImageView?,
    image: String?,
    placeHolder: Int
) {
    // Log.e("xloadImages", "My IMAGE ::: ${image}")
    image?.let {
        if (it.contains("http")) {
            Picasso.get()
                .load(it)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(view)
        } else {
            val file = File(it)
            Picasso.get()
                .load(file)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(view)
        }
    } ?: run {
        Picasso.get()
            .load(R.drawable.ic_select_image)
            .error(R.drawable.ic_select_image)
            .placeholder(R.drawable.ic_select_image)
            .into(view)
    }
}
