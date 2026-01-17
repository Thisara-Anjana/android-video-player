package com.example.androidvideoplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidvideoplayer.R
import android.content.Intent
import com.example.androidvideoplayer.ui.activity.SettingsActivity

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)
        
        view.findViewById<View>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        
        view.findViewById<View>(R.id.btn_theme).setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            intent.putExtra("theme_setup", true) // Pass signal to open theme dialog immediately
            startActivity(intent)
        }
        
        return view
    }
}
