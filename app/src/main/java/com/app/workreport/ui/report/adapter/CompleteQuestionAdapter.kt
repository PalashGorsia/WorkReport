package com.app.workreport.ui.report.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.R
import com.app.workreport.databinding.ItemTodoListBodyBinding
import com.app.workreport.model.Data2
import com.app.workreport.model.Todo2
import com.app.workreport.ui.checklist.dialog.AddCommentsDialogFragment
import com.app.workreport.ui.checklist.dialog.InfoDialogFragment
import com.app.workreport.util.setImageInText

class CompleteQuestionAdapter(
    private val context: FragmentActivity,
    var  dataM: Data2?,
    private val onClick: (entity: Todo2?, pos: Int, type:Int, comment:String, imagePath:(path:String) ->Unit) -> Unit
) :
    RecyclerView.Adapter<CompleteQuestionAdapter.AddDietPlanViewHolder>() {
    private var list:ArrayList<Todo2> = ArrayList()
    var titlePo = -1
    var tabPo = -1
    var date:String =""

    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: List<Todo2>, adapterPosition: Int,tabPo:Int) {
        this.list.clear()
        this.list.addAll(list)
        titlePo = adapterPosition
        this.tabPo =tabPo
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
            binding.apply {
                isEditable = false
                indexTV.text = "${absoluteAdapterPosition+1}"
                btSubmit.visibility = View.GONE

                if (entity.is_important) {
                    setImageInText(context, entity.check_item, titleTV)
                    btNotChecked.visibility = View.INVISIBLE
                } else {
                    titleTV.text = entity.check_item
                    btNotChecked.visibility = View.VISIBLE
                }
                if (entity.NG.equals("1",true)){
                    setNGChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }else{
                    //
                }
                if (entity.NC.equals("1",true)){
                    setNCChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }else{
                    //
                }
                if (entity.O.equals("1",true)){
                    setOChecked(this)
                    backgroundCL.setBackgroundColor(Color.parseColor("#FFFFF1"))
                }else{
                    //
                }
                if (entity.comment.isNotEmpty()){
                    setBackgroundTint(ibEdit)
                }else{
//
                }
                if (entity.image_url.isNotEmpty()){
                    setBackgroundTint(ibCamera)
                }else{
//
                }


                ibGuide.setOnClickListener {
                    dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(absoluteAdapterPosition)?.let { to ->
                        val info = InfoDialogFragment.newInstance(to.reference_example?:"")
                        info.show(context.supportFragmentManager, "")
                    }


                }



                ibCamera.setOnClickListener {
                    onClick(entity,absoluteAdapterPosition,2,""){
                    }
                }

                    ibEdit.setOnClickListener {
                        dataM?.tabs?.get(tabPo)?.content?.get(titlePo)?.todos?.get(absoluteAdapterPosition)?.let { to ->
                            val text = to.comment
                            if (text.isNotEmpty()){
                                val addComment = AddCommentsDialogFragment.
                                newInstance(text, 0){
                                        _, _ ->
                                }
                                addComment.show(context.supportFragmentManager, "")

                            }

                        }
                    }
                executePendingBindings()

            }
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
/*
    private fun setBackgroundTint2(view: View?) {
        view!!.backgroundTintList = ContextCompat.getColorStateList(
            context,
            R.color.grey_li
        )
    }
*/
}