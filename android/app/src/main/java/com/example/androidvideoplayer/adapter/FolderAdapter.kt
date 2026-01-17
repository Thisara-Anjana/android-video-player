package com.example.androidvideoplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvideoplayer.data.model.VideoFolder
import com.example.androidvideoplayer.databinding.ItemFolderBinding

class FolderAdapter(private val onFolderClick: (VideoFolder) -> Unit) :
    ListAdapter<VideoFolder, FolderAdapter.FolderViewHolder>(FolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: VideoFolder) {
            binding.folderName.text = folder.name
            binding.videoCount.text = "${folder.videos.size} videos"
            binding.root.setOnClickListener { onFolderClick(folder) }
        }
    }

    class FolderDiffCallback : DiffUtil.ItemCallback<VideoFolder>() {
        override fun areItemsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoFolder, newItem: VideoFolder): Boolean {
            return oldItem == newItem
        }
    }
}
