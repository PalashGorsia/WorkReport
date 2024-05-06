package com.app.workreport.ui.leave.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.databinding.ItemViewLeaveListBinding
import com.app.workreport.model.LeaveData

class LeavePagingAdapter(private val onClick: (entity: LeaveData, pos: Int) -> Unit)
    : PagingDataAdapter<LeaveData,LeavePagingAdapter.LeaveVH>(LeaveComparator){
    private var currentPo = -1
    private var isClicked = false
    object LeaveComparator : DiffUtil.ItemCallback<LeaveData>() {
        override fun areItemsTheSame(oldItem: LeaveData, newItem: LeaveData) =
            oldItem._id == newItem._id

        override fun areContentsTheSame(oldItem: LeaveData, newItem: LeaveData) =
            oldItem == newItem
    }

    override fun onBindViewHolder(holder: LeavePagingAdapter.LeaveVH, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeavePagingAdapter.LeaveVH {
        return LeaveVH(ItemViewLeaveListBinding.inflate(LayoutInflater.from(
            parent.context), parent, false))
    }

    inner class LeaveVH(val binding: ItemViewLeaveListBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
        fun bind(entity: LeaveData) {
            binding.apply {
                /* todo only save in - progress data in database */
                /*if (absoluteAdapterPosition>5)
                     notifyItemChanged(4)*/
                leaveList = entity
                if (isClicked){
                    showCancel = currentPo==absoluteAdapterPosition
                }

                deleteIcon.setOnClickListener {
                    onClick(entity,absoluteAdapterPosition)
                }
                cardView.setOnClickListener {
                    currentPo = absoluteAdapterPosition
                    isClicked = true
                    if (currentPo == absoluteAdapterPosition) {
                        if (deleteIcon.isVisible)
                            currentPo = -1

                        notifyDataSetChanged()

                    }
                }
            }
            binding.executePendingBindings()
        }
    }


}