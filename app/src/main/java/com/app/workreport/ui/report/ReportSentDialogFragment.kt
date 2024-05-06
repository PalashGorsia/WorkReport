package com.app.workreport.ui.report

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.app.workreport.R
import com.app.workreport.util.BUTTON_TEXT
import com.app.workreport.util.MESSAGE
import com.app.workreport.util.SHOW_CANCEL
import com.app.workreport.util.TITLE


class ReportSentDialogFragment(val showIcon: Boolean,val callback:(status:Boolean)->Unit)
    : DialogFragment(), View.OnClickListener {


  //  private var callback: AlertInterface? = null
    private var title: String? = ""
    private var message: String? = ""
    private var buttonText: String? = ""
    private var showCancel: Boolean = false

    companion object {
        @JvmStatic
        fun newInstance(
            title: String,
            message: String,
            showCancel: Boolean,
            buttonText: String,
            showIcon:Boolean = true,
            callback:(status:Boolean)->Unit
        ) =
            ReportSentDialogFragment(showIcon,callback).apply {
                arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(MESSAGE, message)
                    putBoolean(SHOW_CANCEL, showCancel)
                    putString(BUTTON_TEXT, buttonText)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_report_sent_dialog, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().let {
            title = it.getString(TITLE)
            message = it.getString(MESSAGE)
            showCancel = it.getBoolean(SHOW_CANCEL)
            buttonText = it.getString(BUTTON_TEXT)

        }
        if (title.equals(resources.getString(R.string.inspection),true))
            view.findViewById<ImageView>(R.id.imageView).isVisible = false
        if (!showIcon)
            view.findViewById<ImageView>(R.id.imageView).isVisible = false

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val closeBT = view.findViewById<Button>(R.id.btClose)
        val ivClose = view.findViewById<AppCompatImageView>(R.id.ivCross)

        closeBT.setOnClickListener(this)
        ivClose.setOnClickListener(this)

        if (title?.isEmpty() == true) {
            tvTitle.visibility = View.GONE
            view.findViewById<ImageView>(R.id.imageView).visibility = View.GONE
        }
        tvTitle.text = title
        tvMessage.text = message
        closeBT.text = buttonText

        if (showCancel)
            ivClose.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.btClose) {
            callback(true)
            dismiss()
        }
        if (i == R.id.ivCross) {
            dismiss()
            callback(true)
/*
            if(callback != null)
            {
                callback!!.buttonNoClick()
            }
*/
        }
    }

}


