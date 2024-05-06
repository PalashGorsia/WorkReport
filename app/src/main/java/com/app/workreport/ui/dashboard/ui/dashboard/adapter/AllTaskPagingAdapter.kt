package com.app.workreport.ui.dashboard.ui.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.R
import com.app.workreport.data.model.AllTasksData
import com.app.workreport.data.model.SaveShootingParts
import com.app.workreport.databinding.ItemViewDashboardBinding
import com.app.workreport.model.DataTask
import com.app.workreport.ui.dashboard.ui.dashboard.DashboardViewModel
import com.app.workreport.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllTaskPagingAdapter(private val context: Context,private val dashboardViewModel: DashboardViewModel,private val onClick: (entity: DataTask, pos: Int) -> Unit)
    : PagingDataAdapter<DataTask,AllTaskPagingAdapter.UserEventVH>(UserEventComparator){
    private var statusLineBreck = false

    object UserEventComparator : DiffUtil.ItemCallback<DataTask>() {
        override fun areItemsTheSame(oldItem: DataTask, newItem: DataTask) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: DataTask, newItem: DataTask) =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: AllTaskPagingAdapter.UserEventVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllTaskPagingAdapter.UserEventVH {
        return UserEventVH(ItemViewDashboardBinding.inflate(LayoutInflater.from(
            parent.context), parent, false))
    }

    inner class UserEventVH(val binding: ItemViewDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(entity: DataTask) {
            binding.apply {
                /*todo only save in - progress data in database */
                if (entity._id.isNotEmpty()&&entity.job.progressStatus==IN_PROGRESS_STATUS||entity.job.progressStatus==WORK_PLAN_STATUS){
                    dashboardViewModel.viewModelScope.launch {
                        dashboardViewModel.insertWorkPlan(AllTasksData(entity._id,
                            entity.date?:"",entity?.customer?.name?:"",entity?.facility?.name?:"",
                            entity.job.address,entity.job.name,entity.job._id,entity.assignedTo?._id?:"",entity.job.progressStatus))
                        entity.shooting?.let {
                            for (i in it.indices){
                                dashboardViewModel.insertShootingData(
                                    SaveShootingParts(
                                        entity._id,it[i]._id,it[i].targetName,it[i].shootingTimeBefore?.visible?:false,
                                        it[i].shootingTimeAfter?.visible?:false,it[i].shootingTimeAtWork?.visible?:false
                                    )
                                )
                            }
                        }
                    }
                }
                taskList = entity
                 dashboardViewModel.viewModelScope.launch {
                     if (entity.job.progressStatus== IN_PROGRESS_STATUS||entity.job.progressStatus== WORK_PLAN_STATUS){
                         try {
                             xtnLog("is in progers")
                             dashboardViewModel.reportRepository.getImageByWorkoutId(entity._id)
                                 .collectLatest { list ->
                                     if (list.isNotEmpty()){
                                         imageCount.isVisible =list.isNotEmpty()
                                         showImageCount =list.isNotEmpty()
                                         if (list.filter { it.imageUrl.isNotEmpty()  }.size==list.size){
                                             imageCount.setTextColor(context.resources.getColor(R.color.green,context.resources.newTheme()))
                                         }else{
                                             imageCount.setTextColor(context.resources.getColor(R.color.text_yellow_dr,context.resources.newTheme()))
                                         }
                                         if (list.isNotEmpty())
                                             if (!statusLineBreck)
                                             imageCount.text ="${list.filter { it.imageUrl.isNotEmpty() }.size} ${context.resources.getString(R.string.photos_uploaded_out_of_break_row)}  ${list.size}"
                                            else
                                                 imageCount.text ="${list.filter { it.imageUrl.isNotEmpty() }.size} ${context.resources.getString(R.string.photos_uploaded_out_of_break_row)}  ${list.size}"
                                         if (imageCount.lineCount>1){
                                                 statusLineBreck = true
                                                 imageCount.text ="${list.filter { it.imageUrl.isNotEmpty() }.size} ${context.resources.getString(R.string.photos_uploaded_out_of_break_row)}  ${list.size}"

                                             }
                                     }else{
                                         imageCount.isVisible =false
                                     }
                                 }

                         }catch (e:Exception){
                             imageCount.isVisible =false
                             showImageCount = false
                         }
                     }else{
                         imageCount.isVisible =false
                         showImageCount = false
                     }
                 }

                entity.job.progressStatus.let {
                    if (it== IN_PROGRESS_STATUS||it== WORK_PLAN_STATUS){
                        status.text = "${context.resources.getString(R.string.inprogress)}"
                        status.setTextColor(context.resources.getColor(R.color.text_yellow))
                    }else{
                        status.text = "${context.resources.getString(R.string.completed)}"
                        status.setTextColor(context.resources.getColor(R.color.green))
                    }
                }



                itemView.setOnClickListener {
                    onClick(entity,absoluteAdapterPosition)
                }
            }
            binding.executePendingBindings()
        }
    }
}