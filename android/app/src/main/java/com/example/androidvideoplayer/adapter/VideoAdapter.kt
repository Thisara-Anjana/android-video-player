package com.example.androidvideoplayer.adapter

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.androidvideoplayer.data.model.Video
import com.example.androidvideoplayer.databinding.ItemVideoBinding
import java.util.concurrent.TimeUnit

class VideoAdapter(private val onVideoClick: (Video) -> Unit) :
    ListAdapter<Video, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(video: Video) {
            binding.titleTextView.text = video.name
            binding.durationTextView.text = formatDuration(video.duration)
            binding.sizeTextView.text = Formatter.formatFileSize(binding.root.context, video.size)
            
            // Load video thumbnail - Coil will handle it automatically
            binding.thumbnailImageView.load(video.uri) {
                crossfade(true)
                placeholder(android.R.drawable.ic_media_play)
                error(android.R.drawable.stat_notify_error)
                // Force exact precision for perfect quality
                precision(coil.size.Precision.EXACT)
                // Use maximum caching
                memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                diskCachePolicy(coil.request.CachePolicy.ENABLED)
                // Decode assuming full quality
                decoderFactory(coil.decode.VideoFrameDecoder.Factory())
            }

            binding.root.setOnClickListener {
                onVideoClick(video)
            }
        }




        private fun formatDuration(durationMillis: Long): String {
            val hours = TimeUnit.MILLISECONDS.toHours(durationMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60
            
            return if (hours > 0) {
                String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Video, newItem: Video): Boolean {
            return oldItem == newItem
        }
    }
}
