package com.example.androidvideoplayer.ui.activity

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.ScrollView
import android.graphics.Color
import android.view.Gravity

class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val error = intent.getStringExtra("error") ?: "Unknown error"
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#121212")) // Dark background
            setPadding(32, 32, 32, 32)
        }
        
        val title = TextView(this).apply {
            text = "⚠️ APP CRASHED"
            textSize = 24f
            setTextColor(Color.RED)
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 0, 0, 16)
        }
        
        val message = TextView(this).apply {
            text = "Please send this error to the developer:"
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(0, 0, 0, 16)
        }
        
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.parseColor("#1E1E1E"))
            setPadding(16, 16, 16, 16)
        }
        
        val errorText = TextView(this).apply {
            text = error
            setTextColor(Color.parseColor("#FF8A80")) // Light red
            textSize = 12f
            typeface = android.graphics.Typeface.MONOSPACE
        }
        
        scrollView.addView(errorText)
        
        val copyButton = Button(this).apply {
            text = "COPY ERROR LOG"
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
            setOnClickListener {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Crash Log", error)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this@CrashActivity, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
        
        layout.addView(title)
        layout.addView(message)
        layout.addView(scrollView)
        layout.addView(copyButton)
        
        setContentView(layout)
    }
}
