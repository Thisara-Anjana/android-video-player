package com.example.androidvideoplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidvideoplayer.R

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvideoplayer.adapter.AudioAdapter
import com.example.androidvideoplayer.viewmodel.VideoViewModel
import android.content.Intent
import com.example.androidvideoplayer.PlayerActivity

class AudioFragment : Fragment() {
    
    private lateinit var viewModel: VideoViewModel
    private lateinit var audioAdapter: AudioAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audio, container, false)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewAudio)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        audioAdapter = AudioAdapter { audio ->
            // Play audio using PlayerActivity (reusing video player for now)
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("videoUri", audio.path)
            intent.putExtra("videoTitle", audio.title)
            startActivity(intent)
        }
        recyclerView.adapter = audioAdapter
        
        viewModel = ViewModelProvider(requireActivity())[VideoViewModel::class.java]
        viewModel.audioFiles.observe(viewLifecycleOwner) { files ->
            audioAdapter.submitList(files)
        }
        
        return view
    }
}
