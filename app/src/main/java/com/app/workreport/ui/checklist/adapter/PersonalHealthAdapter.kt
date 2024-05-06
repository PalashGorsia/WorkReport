package com.app.workreport.ui.checklist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.app.workreport.databinding.ItemViewExrBinding
import com.app.workreport.model.*
import com.app.workreport.ui.checklist.CheckListViewModel
import kotlinx.coroutines.launch


class PersonalHealthAdapter(
    private val context: FragmentActivity,
    val viewModel: CheckListViewModel,
    val onClick :(entry: Todo2?, pos:Int, posChild:Int, type:Int, comment:String, imagePath:(path:String) ->Unit) ->Unit
) : RecyclerView.Adapter<PersonalHealthAdapter.AddDietPlanViewHolder>() {
    private var currentPo = -1
    private var isClicked = false
    var isLast: Boolean = false
    var lastGroupTitle: String = ""
    var lastItemIndex: Int = 0

    private lateinit var questionAdapter: QuestionAdapter
    private var groups:ArrayList<Content> = ArrayList()
    private var dataM: Data? =null
    private var array: ArrayList<Content> = ArrayList()
    private var tabPo =-1
    private var mDate:String =""
//InspectionContent

    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: Data, pos: Int) {
        dataM = Data(AdditionalInfo("",""),ArrayList())
        //dataM = Data(AdditionalInfo("",""),ArrayList())
        dataM = list
        try {
            dataM?.let {
                array = it.tabs[pos].content as ArrayList<Content>
                this.groups.clear()
                this.groups = array
                tabPo =pos
              //  mDate = if (isYesterdayDate) yesterdayDate else currentDateFormat
                notifyDataSetChanged()
            }

        }catch (e:Exception){

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddDietPlanViewHolder {
        return AddDietPlanViewHolder(
            ItemViewExrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: AddDietPlanViewHolder, position: Int) {

        groups[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    inner class AddDietPlanViewHolder(val binding: ItemViewExrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
        fun bind(entity: Content) {

            binding.apply {
                lifecycleOwner = context
             //   data =entity
                headerTitle.text = entity.name
                rvQuestion.setHasFixedSize(true)
                questionAdapter = QuestionAdapter(context, false,viewModel,dataM){
                        entit, pos, type, comment,imagePath ->
                    when(type){
                        0->{
                            onClick(entit,absoluteAdapterPosition,112,0,""){
                                imagePath("01")
                            }

                        }
                        1->{
                            onClick(entit,absoluteAdapterPosition,pos,1,""){
                                imagePath(it)
                            }
                        }
                        2->{
                            onClick(entit,absoluteAdapterPosition,pos,2,""){
                                imagePath(it)
                            }

                            //  imagePath("123456")
                        }
                        4->{
                            //add adistional comment
                            onClick(entit,absoluteAdapterPosition,pos,4,comment){
                                imagePath(it)
                            }

                            //  imagePath("123456")
                        }
                    }

                }
                rvQuestion.adapter = questionAdapter
                if (isClicked){
                    if (currentPo==absoluteAdapterPosition){
                       // xtnLog("show $currentPo $adapterPosition")
                        rvQuestion.visibility = View.VISIBLE
                        getDataFormDatabase(questionAdapter,absoluteAdapterPosition)
                     //   questionAdapter.swapList(entity.todos,adapterPosition,tabPo,mDate)
                    }else{
                      //  xtnLog("hide $currentPo $adapterPosition")
                        rvQuestion.visibility = View.GONE
                        questionAdapter.swapList(ArrayList(),absoluteAdapterPosition,tabPo,mDate)
                    }
                }
                layoutHader.setOnClickListener {
                    currentPo = absoluteAdapterPosition
                    isClicked =true
                   // previousSelectedPo = adapterPosition
                    if (currentPo==absoluteAdapterPosition){
                        if (rvQuestion.visibility==View.VISIBLE){
                            currentPo =-1
                           // questionAdapter.swapList(entity.items as List<Todo>)
                        }else{
                        //    questionAdapter.swapList(ArrayList())
                        }
                        notifyDataSetChanged()
                    }
                }
                executePendingBindings()

            }

        }

    }
    /*todo for show Submit button when tab on last question*/
    fun isLast(isLast: Boolean, lastGroupTitle: String, lastItemIndex: Int) {
        this.isLast = isLast
        this.lastGroupTitle = lastGroupTitle
        this.lastItemIndex = lastItemIndex
    }

    private fun getDataFormDatabase(questionAdapter: QuestionAdapter, pos: Int) {
        context.lifecycleScope.launch {
         //   xtnLog("Date $mDate")
           val it =  viewModel.getCheckListBasedOnJob()
           val dataM2 = Gson().fromJson(it.json, Data::class.java)
           val cc =  dataM2.tabs[tabPo].content as ArrayList<Content>
           questionAdapter.swapList(cc[pos].todos,pos,tabPo,mDate)
        }
    }


}