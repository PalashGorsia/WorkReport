package com.app.workreport.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.workreport.databinding.LayoutAdapterLoadStateBinding

class LoaderStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder(LayoutAdapterLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false), retry)
    }

    class LoaderViewHolder(val binding: LayoutAdapterLoadStateBinding, retry: () -> Unit) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetryLoadState.setOnClickListener {
                retry()
            }
        }

        fun bind(loadState: LoadState) {
            when(loadState){
                LoadState.Loading->{
                    binding.progressLoadState.isVisible=true
                    binding.layRetry.isVisible=false
                }
                else->{
                    binding.progressLoadState.isVisible=false
                    binding.layRetry.isVisible=true
                }
            }

        }
    }
}