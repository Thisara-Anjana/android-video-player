package com.example.androidvideoplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidvideoplayer.R

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Setup Quick Actions
        view.findViewById<View>(R.id.card_action_videos).setOnClickListener {
            // Navigate to Videos Tab
            findNavController().navigate(R.id.nav_videos)
        }
        
        view.findViewById<View>(R.id.card_action_folders).setOnClickListener {
            // Navigate to Folders Tab
            findNavController().navigate(R.id.nav_folders)
        }
        
        view.findViewById<View>(R.id.card_action_audio).setOnClickListener {
            // Navigate to Audio Tab
            findNavController().navigate(R.id.nav_audio)
        }
        
        view.findViewById<View>(R.id.card_action_playlist).setOnClickListener {
            // Navigate to Playlists (Feature TODO: implement playlist fragment? For now Toast)
            Toast.makeText(requireContext(), "Playlists Coming Soon", Toast.LENGTH_SHORT).show()
        }
        
        view.findViewById<View>(R.id.btn_search).setOnClickListener {
            Toast.makeText(requireContext(), "Search Clicked", Toast.LENGTH_SHORT).show()
        }
        
        return view
    }
}
