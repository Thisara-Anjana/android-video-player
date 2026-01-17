package com.example.androidvideoplayer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvideoplayer.R
import com.example.androidvideoplayer.viewmodel.VideoViewModel

class AudioAdapter(private val onAudioClick: (VideoViewModel.AudioFile) -> Unit) :
    ListAdapter<VideoViewModel.AudioFile, AudioAdapter.AudioViewHolder>(AudioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
        // Using simple_list_item_2 for Title + Artist
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = getItem(position)
        holder.bind(audio)
    }

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1: TextView = itemView.findViewById(android.R.id.text1)
        private val text2: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(audio: VideoViewModel.AudioFile) {
            text1.text = audio.title
            val minutes = (audio.duration / 1000) / 60
            val seconds = (audio.duration / 1000) % 60
            text1.setTextColor(android.graphics.Color.WHITE)
            
            text2.text = "${audio.artist} • %02d:%02d".format(minutes, seconds)
            text2.setTextColor(android.graphics.Color.LTGRAY)
            
            itemView.setOnClickListener { onAudioClick(audio) }
        }
    }

    class AudioDiffCallback : DiffUtil.ItemCallback<VideoViewModel.AudioFile>() {
        override fun areItemsTheSame(oldItem: VideoViewModel.AudioFile, newItem: VideoViewModel.AudioFile): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: VideoViewModel.AudioFile, newItem: VideoViewModel.AudioFile): Boolean {
            return oldItem == newItem
        }
    }
}
