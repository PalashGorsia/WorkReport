package com.app.workreport.ui.dashboard.ui.gallery.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.data.model.ImageData
import com.app.workreport.databinding.ItemViewGalleryBinding
import com.app.workreport.util.xtnLog
import java.lang.Exception

class GalleryAdapter(
    private val context: Context,
    private val onClick: (entity: ImageData, pos: Int, type:Int) -> Unit
) :
    RecyclerView.Adapter<GalleryAdapter.AddDietPlanViewHolder>() {
    private var list = mutableListOf<ImageData>()
    private var lastSelectedPosition = -1
    private var isClick = false
    @SuppressLint("NotifyDataSetChanged")
    fun swapList(list: List<ImageData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AddDietPlanViewHolder(
            ItemViewGalleryBinding.inflate(
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

    inner class AddDietPlanViewHolder(val binding: ItemViewGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
        fun bind(entity: ImageData) {
            binding.apply {
                imageData = entity.imagePath
                 checkBox.setOnClickListener(null)
                 checkBox.setOnTouchListener(null)
                if (!isClick){
                    if (entity.isSelected){
                        lastSelectedPosition =position
                        isImageSelected = true
                        checkBox.isChecked = true
                        onClick(entity,absoluteAdapterPosition,0)
                    }
                }
                try {
                    if (isClick)
                        checkBox.isChecked = lastSelectedPosition == absoluteAdapterPosition

                        isImageSelected =lastSelectedPosition==absoluteAdapterPosition
                }catch (e:Exception){
                    xtnLog("Gallery ${e.message}","Gallery")
                }

                try {
                    checkBox.setOnClickListener {
                        if (checkBox.isChecked){
                            isClick = true
                            lastSelectedPosition =absoluteAdapterPosition
                            onClick(entity,position,0)
                            //  onCliclk(entity,absoluteAdapterPosition,1)
                            isImageSelected = true
                            notifyDataSetChanged()
                        }else{
                            isClick =true
                            onClick(entity,position,1)
                            isImageSelected = false
                            lastSelectedPosition = -1
                        }
/*
                        checkBox.setOnCheckedChangeListener{ compoundButton: CompoundButton?, b: Boolean ->
                            if (b){
                                isClick = true
                                lastSelectedPosition =absoluteAdapterPosition
                                onClick(entity,position,0)
                                //  onCliclk(entity,absoluteAdapterPosition,1)
                                isImageSelected = true
                                notifyDataSetChanged()
                            }else{
                                isClick =true
                                isImageSelected = false
                                onClick(entity,position,1)
                                lastSelectedPosition = -1
                            }
                        }
*/

                    }

                }catch (e:Exception){
                    xtnLog("Gallery ${e.message}","Gallery")
                }



                //  if (!isEditable){
                //      btnAddMore.isVisible = isEditable
                //  }
//               btnAddMore.setOnClickListener {
//                   onCliclk(entity,absoluteAdapterPosition,0)
//               }
            }
        }


    }

}