package com.app.workreport.ui.dashboard.ui.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.databinding.ItemViewReportBinding
import com.app.workreport.model.DataTask

class AllCompleteTaskPagingAdapter(private val context: Context, private val onClick: (entity: DataTask, pos: Int) -> Unit)
    : PagingDataAdapter<DataTask,AllCompleteTaskPagingAdapter.UserEventVH>(UserEventComparator){

    object UserEventComparator : DiffUtil.ItemCallback<DataTask>() {
        override fun areItemsTheSame(oldItem: DataTask, newItem: DataTask) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: DataTask, newItem: DataTask) =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: AllCompleteTaskPagingAdapter.UserEventVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllCompleteTaskPagingAdapter.UserEventVH {
        return UserEventVH(ItemViewReportBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    inner class UserEventVH(val binding: ItemViewReportBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(entity: DataTask) {
            binding.apply {
                taskList = entity

                itemView.setOnClickListener {
                    onClick(entity,absoluteAdapterPosition)
                }
            }
            binding.executePendingBindings()
        }
    }
}