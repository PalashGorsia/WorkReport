package com.app.workreport.ui.report.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.databinding.ItemViewExrBinding
import com.app.workreport.model.*

class PersonalHealthCompleteAdapter(
    private val context: FragmentActivity,
    val onClick :(entry: Todo2?, pos:Int, posChild:Int, type:Int, comment:String, imagePath:(path:String) ->Unit) ->Unit
) : RecyclerView.Adapter<PersonalHealthCompleteAdapter.AddDietPlanViewHolder>() {
    private var currentPo = -1
    private var isClicked = false
    private lateinit var questionAdapter:CompleteQuestionAdapter
    private var groups:ArrayList<Content> = ArrayList()
    private var dataM: Data2? =null
    private var array: ArrayList<Content> = ArrayList()
    private var tabPo =-1
    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: Data2, pos: Int) {
       // dataM = Data(AdditionalInfo("",""),ArrayList())
        dataM = Data2("",ArrayList())
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
                questionAdapter =CompleteQuestionAdapter(context,dataM
                ){
                        entit, pos, type, comment,imagePath ->
                    when(type){
                        0->{
                            onClick(entit,adapterPosition,112,0,""){
                                imagePath("01")
                            }

                        }
                        1->{
                            onClick(entit,adapterPosition,pos,1,""){
                                imagePath(it)
                            }
                        }
                        2->{
                            onClick(entit,adapterPosition,pos,2,""){
                                imagePath(it)
                            }

                            //  imagePath("123456")
                        }
                        4->{
                            //add adistional comment
                            onClick(entit,adapterPosition,pos,4,comment){
                                imagePath(it)
                            }

                            //  imagePath("123456")
                        }
                    }

                }
                rvQuestion.adapter = questionAdapter
                if (isClicked){
                    if (currentPo==adapterPosition){
                       // xtnLog("show $currentPo $adapterPosition")
                        rvQuestion.visibility = View.VISIBLE
                       // getDataFormDatabase(questionAdapter,adapterPosition)
                        questionAdapter.swapList(entity.todos,adapterPosition,tabPo)
                    }else{
                      //  xtnLog("hide $currentPo $adapterPosition")
                        rvQuestion.visibility = View.GONE
                        questionAdapter.swapList(ArrayList(),adapterPosition,tabPo)
                    }
                }
                layoutHader.setOnClickListener {
                    currentPo = adapterPosition
                    isClicked =true
                   // previousSelectedPo = adapterPosition
                    if (currentPo==adapterPosition){
                        if (rvQuestion.visibility==View.VISIBLE){
                            currentPo =-1
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

}