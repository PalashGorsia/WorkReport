package com.app.workreport.ui.checklist.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.app.workreport.R
import com.app.workreport.util.ADDITIONAL_COMMENT
import com.app.workreport.util.RECIPIENT_COMMENT


class FinalCommentsFragment(val block:(type:Int,comment:String)->Unit)
    : DialogFragment(), View.OnClickListener {
    private var etFirstComment: EditText? = null
    private var etSecondComment: EditText? = null
  //  private var adapterInterface: AdapterInterface<Int, String>? = null

    companion object {

        @JvmStatic
        fun newInstance(additionalComment: String, recipientComment: String,block:(type:Int,comment:String)->Unit) =
            FinalCommentsFragment(block).apply {
                arguments = Bundle().apply {
                    putString(ADDITIONAL_COMMENT, additionalComment)
                    putString(RECIPIENT_COMMENT, recipientComment)
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
        //  dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_final_add_comment, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener(this)
        view.findViewById<Button>(R.id.btSubmit).setOnClickListener(this)

        val additionalComment = requireArguments().getString(ADDITIONAL_COMMENT)
        val recipientComment = requireArguments().getString(RECIPIENT_COMMENT)
        etFirstComment = view.findViewById(R.id.etFirstComment)
        etFirstComment!!.setText(additionalComment)

        etSecondComment = view.findViewById(R.id.etSecondComment)
        etSecondComment!!.setText(recipientComment)

    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ivCross)
            observeComments(0)
        if (i == R.id.btSubmit)
            observeComments(1)
    }

    private fun observeComments(type : Int) {

        val mSecondComment = etSecondComment!!.text.toString()
        val mFirstComment = etFirstComment!!.text.toString()
        if (mFirstComment.isNotEmpty() && mSecondComment.isNotEmpty())
            block(type, "$mFirstComment@$mSecondComment")
        else
            block(type, "${""}@${""}")
         //   showSnackBar(etSecondComment!!, getString(R.string.additional_comments))
        dismiss()
    }

/*
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }
*/

}


