package com.example.androidvideoplayer.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidvideoplayer.R
import com.example.androidvideoplayer.adapter.VideoAdapter
import com.example.androidvideoplayer.viewmodel.VideoViewModel
import com.example.androidvideoplayer.PlayerActivity

class VideoListActivity : AppCompatActivity() {

    private lateinit var viewModel: VideoViewModel
    private lateinit var adapter: VideoAdapter
    private var recyclerView: RecyclerView? = null
    private var titleView: TextView? = null

    private fun showSortDialog() {
        val options = arrayOf("Name (A-Z)", "Size (Large-Small)", "Date (New-Old)")
        android.app.AlertDialog.Builder(this)
            .setTitle("Sort By")
            .setItems(options) { _, which ->
                val currentList = adapter.currentList.toMutableList()
                val sortedList = when (which) {
                    0 -> currentList.sortedBy { it.name }
                    1 -> currentList.sortedByDescending { it.size }
                    2 -> currentList.sortedByDescending { it.dateAdded }
                    else -> currentList
                }
                adapter.submitList(sortedList)
            }
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_list)
        
        val folderName = intent.getStringExtra("folderName") ?: "Videos"
        
        recyclerView = findViewById(R.id.videoList)
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        
        toolbar.title = folderName
        
        // Add Sort Button
        val sortBtn = android.widget.ImageButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_sort_by_size)
            
            // Fix: Resolve attribute properly
            val outValue = android.util.TypedValue()
            theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
            setBackgroundResource(outValue.resourceId)
            
            setColorFilter(android.graphics.Color.WHITE)
            layoutParams = androidx.appcompat.widget.Toolbar.LayoutParams(
                androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT,
                androidx.appcompat.widget.Toolbar.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.END
            )
            setOnClickListener { showSortDialog() }
        }
        toolbar.addView(sortBtn)
        
        toolbar.setNavigationOnClickListener {
            finish()
        }
        
        viewModel = ViewModelProvider(this)[VideoViewModel::class.java]
        
        adapter = VideoAdapter { video ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("videoUri", video.uri.toString())
            intent.putExtra("videoTitle", video.name)
            intent.putExtra("folderName", folderName)
            startActivity(intent)
        }
        
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        
        viewModel.allVideos.observe(this) { allVideos ->
            val folderVideos = allVideos.filter { it.folderName == folderName }
            if (folderVideos.isNotEmpty()) {
                adapter.submitList(folderVideos)
            } else {
                 adapter.submitList(allVideos)
            }
        }
        
        viewModel.loadVideos()
    }
}
