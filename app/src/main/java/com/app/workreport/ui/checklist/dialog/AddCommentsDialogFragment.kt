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

import com.app.workreport.util.DATA
import com.app.workreport.util.TYPE
import com.app.workreport.util.xtnToast


class AddCommentsDialogFragment(val block:(type:Int,comment:String)->Unit)
    : DialogFragment(), View.OnClickListener {


    private var etComment: EditText? = null
   // private var adapterInterface: AdapterInterface<Int, String>? = null

    companion object {

        @JvmStatic
        fun newInstance(data: String, reportType: Int,block:(type:Int,comment:String)->Unit) =
            AddCommentsDialogFragment(block).apply {
                arguments = Bundle().apply {
                    putString(DATA, data)
                    putInt(TYPE, reportType)
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
        return inflater.inflate(R.layout.dialog_add_comment, container)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageView>(R.id.ivCross).setOnClickListener(this)
        val btSubmit = view.findViewById<Button>(R.id.btSubmit)
        btSubmit.setOnClickListener(this)

        val data = requireArguments().getString(DATA)
        val reportType = requireArguments().getInt(TYPE)
        etComment = view.findViewById<EditText>(R.id.etComment)
        etComment!!.setText(data)

        if (reportType == 0) {
            etComment!!.isEnabled = false
            btSubmit.visibility = View.GONE
        }else{
            etComment!!.isEnabled = true
            btSubmit.visibility = View.VISIBLE
        }

    }

    override fun onClick(v: View?) {
        val i = v!!.id
        if (i == R.id.ivCross) {
            dismiss()
        }
        if (i == R.id.btSubmit) {
            val string = etComment!!.text.toString()
            if (string.isEmpty()) {
                requireContext().xtnToast(resources.getString(R.string.pls_add_comment))
            } else {
                block(1,string)
                dismiss()
            }
               /* if (string.length < 30) {
              //  showSnackBar(etComment!!, getString(R.string.comment_length))
            } else {
                block(1,string)
                dismiss()
            }*/
        }
    }

    /*
    fun setCallback(adapterInterface: AdapterInterface<Int, String>) {

        this.adapterInterface = adapterInterface
    }
*/
}


