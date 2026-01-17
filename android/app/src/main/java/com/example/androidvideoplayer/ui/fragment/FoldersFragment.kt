package com.example.androidvideoplayer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.androidvideoplayer.R
import com.example.androidvideoplayer.viewmodel.VideoViewModel
import com.example.androidvideoplayer.adapter.FolderAdapter
import android.content.Intent
import com.example.androidvideoplayer.ui.activity.VideoListActivity

class FoldersFragment : Fragment() {

    private lateinit var viewModel: VideoViewModel
    private lateinit var folderAdapter: FolderAdapter
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_folders, container, false)
        
        recyclerView = view.findViewById(R.id.recyclerViewFolders)
        
        // Setup ViewModel from Activity scope or Fragment scope? Activity scope is better for shared data
        viewModel = ViewModelProvider(requireActivity())[VideoViewModel::class.java]
        
        folderAdapter = FolderAdapter { folder ->
            val intent = Intent(requireContext(), VideoListActivity::class.java)
            intent.putExtra("folderName", folder.name)
            startActivity(intent)
        }
        
        recyclerView?.adapter = folderAdapter
        
        viewModel.videoFolders.observe(viewLifecycleOwner) { folders ->
            folderAdapter.submitList(folders)
        }
        
        return view
    }
    
    override fun onResume() {
        super.onResume()
        updateLayoutManager()
        // Trigger load if empty? ViewModel should handle
    }
    
    private fun updateLayoutManager() {
        val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val isGrid = prefs.getBoolean("grid_view", true)
        
        if (isGrid) {
            recyclerView?.layoutManager = GridLayoutManager(context, 2)
        } else {
            recyclerView?.layoutManager = LinearLayoutManager(context)
        }
    }
}
