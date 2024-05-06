package com.app.workreport.ui.dashboard.ui.dashboard.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.R
import com.app.workreport.data.model.AllTasksData
import com.app.workreport.databinding.ItemViewDashboardOfflineBinding
import com.app.workreport.ui.dashboard.ui.dashboard.DashboardViewModel
import com.app.workreport.util.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AllTaskAdapter(private val context: Context, private val dashboardViewModel: DashboardViewModel, private val onClick: (entity: AllTasksData, pos: Int) -> Unit)
    : RecyclerView.Adapter<AllTaskAdapter.UserEventVH>(){
    private var list = mutableListOf<AllTasksData>()
    private var statusLineBreck = false
    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: List<AllTasksData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: AllTaskAdapter.UserEventVH, position: Int) {
        holder.bind(list[position])
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllTaskAdapter.UserEventVH {
        return UserEventVH(ItemViewDashboardOfflineBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class UserEventVH(val binding: ItemViewDashboardOfflineBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(entity: AllTasksData) {
            binding.apply {
                 taskList = entity
                 dashboardViewModel.viewModelScope.launch {
                     if (entity.progressStatus== IN_PROGRESS_STATUS||entity.progressStatus== WORK_PLAN_STATUS){
                         try {
                             xtnLog("is in progers")
                             dashboardViewModel.reportRepository.getImageByWorkoutId(entity.taskId)
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

                entity.progressStatus.let {
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

    override fun getItemCount(): Int =list.size
}