package com.app.workreport.ui.checklist.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.app.workreport.R
import com.app.workreport.data.model.QuestionData
import com.app.workreport.databinding.ItemTodoListBodyBinding
import com.app.workreport.model.Data2
import com.app.workreport.model.Todo2
import com.app.workreport.ui.checklist.CheckListViewModel
import com.app.workreport.ui.checklist.dialog.AddCommentsDialogFragment
import com.app.workreport.ui.checklist.dialog.InfoDialogFragment
import com.app.workreport.util.AppPref
import com.app.workreport.util.setImageInText
import kotlinx.coroutines.launch

class QuestionInProgressAdapter(
    private val context: FragmentActivity,
    val isReportSubmitted: Boolean,
    val viewModel: CheckListViewModel,
    var dataM: Data2?,
    private val onClick: (entity: Todo2?, pos: Int, type: Int, comment: String, imagePath: (path: String) -> Unit) -> Unit
) :
    RecyclerView.Adapter<QuestionInProgressAdapter.AddDietPlanViewHolder>() {
    private var list: ArrayList<Todo2> = ArrayList()//mutableListOf<Todo>()
    private var adepterPo = -1

    /// private var dataM:Data2? = data
    var titlePo = -1
    var tabPo = -1
    var dataStatus = 2
    var date: String = ""

    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: List<Todo2>, absoluteAdapterPosition: Int, tabPo: Int, mDate: String) {
        this.list.clear()
        this.list.addAll(list)
        // dataM = data
        titlePo = absoluteAdapterPosition
        this.tabPo = tabPo
        date = mDate
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AddDietPlanViewHolder(
            ItemTodoListBodyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: AddDietPlanViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class AddDietPlanViewHolder(val binding: ItemTodoListBodyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility", "SetTextI18n")
        fun bind(entity: Todo2) {
            if (dataStatus == 2) {
                getDataFormDatabase()
                dataStatus = -1
            }
            //groups?.get(absoluteAdapterPosition)?.items?.get(0)?.O ="1"
            binding.apply {
                // lifecycleOwner =context
                // data = entity
                /* if (absoluteAdapterPosition==0)
                     titleTV.requestFocus()*/
                isEditable = !isReportSubmitted
                indexTV.text = "${absoluteAdapterPosition + 1}"
                // titleTV = entity.check_item//2hrs
                btSubmit.visibility = View.GONE


                if (entity.is_important) {
                    setImageInText(context, entity.check_item, titleTV)
                    btNotChecked.visibility = View.INVISIBLE
                } else {
                    titleTV.text = entity.check_item
                    btNotChecked.visibility = View.VISIBLE
                }
                if (entity.NG.equals("1")) {
                    setNGChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                } else {
                    //
                }
                if (entity.NC.equals("1")) {
                    setNCChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                } else {
                    //
                }
                if (entity.O.equals("1")) {
                    setOChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                } else {
                    //
                }
                if (entity.comment.isNotEmpty()) {
                    setBackgroundTint(ibEdit)
                } else {
//
                }
                if (entity.image_url.isNotEmpty()) {
                    setBackgroundTint(ibCamera)
                } else {
//
                }


                ibGuide.setOnClickListener {
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        val info = InfoDialogFragment.newInstance(to.reference_example?:"")
                        info.show(context.supportFragmentManager, "")
                    }


                }

                btNG.setOnClickListener {
                    adepterPo = absoluteAdapterPosition
                    if (adepterPo == absoluteAdapterPosition)
                        setNGChecked(this)
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        to.NG = "1"
                        to.O = "0"
                        to.NC = "0"
                        to.is_answered = true
                        to.score = if (to.is_important) -1 else 0
                        updateToDB(absoluteAdapterPosition)
                    }
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }

                btNotChecked.setOnClickListener {
                    setNCChecked(binding)
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        to.NG = "0"
                        to.O = "0"
                        to.NC = "1"
                        to.is_answered = true
                        to.score = 0
                        updateToDB(absoluteAdapterPosition)
                        if (!to.is_important)
                            to.score = 0
                    }
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }

                ibEdit.setOnClickListener { view ->
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        val text = to.comment
                        if (text.isEmpty()) {
                            val addComment =
                                AddCommentsDialogFragment.newInstance(text, 1) { _, comment ->
                                    to.comment = comment
                                    setBackgroundTint(view)
                                    updateToDB(absoluteAdapterPosition)
                                }
                            addComment.show(context.supportFragmentManager, "")
                        } else {
                            val addComment =
                                AddCommentsDialogFragment.newInstance(to.comment, 1) { _, comment ->
                                    to.comment = comment
                                    setBackgroundTint(view)
                                    updateToDB(absoluteAdapterPosition)
                                }
                            addComment.show(context.supportFragmentManager, "")
                        }
                    }
                }

                btOK.setOnClickListener {
                    setOChecked(binding)
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        to.NG = "0"
                        to.O = "1"
                        to.NC = "0"
                        to.is_answered = true
                        to.score = 1//if (to.is_important) -1 else 0
                        updateToDB(absoluteAdapterPosition)
                    }
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }

                ibCamera.setOnClickListener {
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(
                        absoluteAdapterPosition
                    )?.let { to ->
                        if (to.image_url.isNotEmpty()) {
                            //  if (!isReportSubmitted){
                            onClick(to, absoluteAdapterPosition, 2, "") {
                                if (it.equals("10")) {
                                    to.image_url = ""
                                    setBackgroundTint2(ibCamera)
                                    updateToDB(absoluteAdapterPosition)
                                }
                            }
                            //   }else{
                            //      onClick(to,absoluteAdapterPosition,1,""){}
                            //  }
                        } else {
                            //if (!isReportSubmitted)
                            onClick(to, absoluteAdapterPosition, 1, "") {
                                to.image_url = it
                                if (it.isNotEmpty())
                                    setBackgroundTint(ibCamera)
                                updateToDB(absoluteAdapterPosition)
                            }
                        }
                    }

                }


/*
                if (isReportSubmitted){
                    ibEdit.setOnClickListener { view->
                        dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(absoluteAdapterPosition)?.let { to ->
                            val text = to.comment?:""
                            if (text.isNotEmpty()){
                                val addComment = AddCommentsDialogFragment.
                                newInstance(text, 0)
*/
/*
                                addComment.setCallback(object : AdapterInterface<Int, String> {
                                    override fun callback(t: Int, string: String) {
                                        to.comment = string
                                        setBackgroundTint(view)
                                        updateToDB()
                                    }
                                })
*//*

                                addComment.show(context.supportFragmentManager, "")

                            }

                        }
                    }

                }else{
                    ibEdit.setOnClickListener { view->
                        dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(absoluteAdapterPosition)?.let { to ->
                           */
/* val addComment = AddCommentsDialogFragment.
                            newInstance(to.comment?:"", 1)
                            addComment.setCallback(object : AdapterInterface<Int, String> {
                                override fun callback(t: Int, string: String) {
                                    to.comment = string
                                    setBackgroundTint(view)
                                    updateToDB()
                                }

                            })
                            addComment.show(context.supportFragmentManager, "")*//*

                        }
                    }
                }
*/
                executePendingBindings()

            }
        }


    }

    private fun getDataFormDatabase() {
        context.lifecycleScope.launch {
            val it = viewModel.getCheckListBasedOnJob()
            dataM = Gson().fromJson(it.json, Data2::class.java)
            Log.e("TAG", "checkListId: ${AppPref.checkListId}")

//            notifyDataSetChanged()
        }

    }


    private fun setNGChecked(binding: ItemTodoListBodyBinding) {
        binding.btNG.isPressed = true
        binding.btNG.isSelected = true

        binding.btNotChecked.isPressed = false
        binding.btNotChecked.isSelected = false

        binding.btOK.isPressed = false
        binding.btOK.isSelected = false
    }

    private fun setNCChecked(binding: ItemTodoListBodyBinding) {
        binding.btNotChecked.isPressed = true
        binding.btNotChecked.isSelected = true

        binding.btNG.isPressed = false
        binding.btNG.isSelected = false

        binding.btOK.isPressed = false
        binding.btOK.isSelected = false
    }

    private fun setOChecked(binding: ItemTodoListBodyBinding) {
        binding.btOK.isPressed = true
        binding.btOK.isSelected = true

        binding.btNG.isPressed = false
        binding.btNG.isSelected = false

        binding.btNotChecked.isPressed = false
        binding.btNotChecked.isSelected = false
    }

    private fun setBackgroundTint(view: View?) {
        view!!.backgroundTintList = ContextCompat.getColorStateList(
            context,
            R.color.purple_200
        )
    }

    private fun setBackgroundTint2(view: View?) {
        view!!.backgroundTintList = ContextCompat.getColorStateList(
            context,
            R.color.grey_li
        )
    }

    private fun updateToDB(pos: Int) {
        val string = Gson().toJson(dataM)
        viewModel.updateCheckList(string)
        dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(pos)?.apply {
            viewModel.insertAnswerData(
                QuestionData(
                    AppPref.checkListId ?: "",
                    reference_example?:"",
                    NC,
                    NG,
                    O,
                    No,
                    Daily ?: "",
                    comment,
                    check_item,
                    is_important,
                    is_answered,
                    image_url,
                    itemId
                )
            )
        }
    }


}