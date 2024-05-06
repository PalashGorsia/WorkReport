package com.app.workreport.ui.checklist.dialog

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.app.workreport.R
import com.app.workreport.util.DATA


open class InfoDialogFragment
    : DialogFragment(), View.OnClickListener {

    companion object {

        @JvmStatic
        fun newInstance(data: String) =
            InfoDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(DATA, data)
                }
            }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.dialog_fragment_info, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val data = requireArguments().getString(DATA) ?: ""
        Log.e("reference body", "reference $data: ")
        val textView = view.findViewById<TextView>(R.id.tvText)
        val textSpannable = SpannableString(data)
        //   val textSpannable = SpannableString("This is a sample paragraph with a URL: http://www.google.com You can visit it!")
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener(this)
        // textView.text = demoText
        //val urlPattern = "(?i)\\b(https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)([^\\s]*)"
        val urlPattern = "https?://[a-zA-Z0-9./-]+"
        val urlMatcher = Regex(urlPattern).findAll(textSpannable ?: "")

        if (!data.contains("http")) {
            textView.text = textSpannable
        } else {
            for (match in urlMatcher) {
                val start = match.range.first
                val end = match.range.last

                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val clickedUrl = match.value
                        val uri = Uri.parse(clickedUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.flags = FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        /*  if (intent.resolveActivity(requireActivity().packageManager) != null) {
                              startActivity(intent)
                          } else {
                              // Handle the case where there's no app to handle the URL
                              // You can display a message to the user or take alternative actions.
                              Log.e(
                                  "InfoDialogFragment",
                                  "No app available to handle the URL: $clickedUrl"
                              )
                          }*/
                    }
                }
                // Set the ClickableSpan to the URL within the text
                textSpannable.setSpan(
                    clickableSpan,
                    start,
                    end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // Make the TextView clickable
                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.text = textSpannable


            }
        }


    }


    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ivCross) {
            dismiss()
        }
    }


}


