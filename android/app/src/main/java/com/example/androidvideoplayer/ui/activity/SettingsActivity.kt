package com.example.androidvideoplayer.ui.activity

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.androidvideoplayer.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
        
        val gridSwitch = findViewById<Switch>(R.id.switch_grid_view)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        
        gridSwitch.isChecked = prefs.getBoolean("grid_view", true)
        
        gridSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("grid_view", isChecked).apply()
        }
        
        // Theme Selection
        val themeLayout = findViewById<android.widget.LinearLayout>(R.id.layout_theme_select)
        themeLayout?.setOnClickListener {
            showThemeDialog()
        }
        
        // PiP Logic
        val pipSwitch = findViewById<Switch>(R.id.switch_pip_mode)
        pipSwitch.isChecked = prefs.getBoolean("pip_mode", true)
        pipSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("pip_mode", isChecked).apply()
        }
        
        // Clear Cache
        findViewById<android.view.View>(R.id.layout_clear_cache).setOnClickListener {
            cacheDir.deleteRecursively()
            android.widget.Toast.makeText(this, "Cache Cleared", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        // About
        findViewById<android.view.View>(R.id.layout_about_app).setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("About Nova Player")
                .setMessage("Version 1.0.0\n\nA professional Android Video Player.\n\nDeveloped with ❤️")
                .setPositiveButton("OK", null)
                .show()
        }
        
        // Check for direct theme intent
        if (intent.getBooleanExtra("theme_setup", false)) {
            showThemeDialog()
        }
    }
    
    private fun showThemeDialog() {
        val themes = arrayOf("Dark Mode (Default)", "Light Mode", "Cyber Future")
        android.app.AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(themes, -1) { dialog, which ->
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                when (which) {
                    0 -> { 
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES) 
                        prefs.edit().putString("app_theme", "dark").apply()
                    }
                    1 -> { 
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO) 
                        prefs.edit().putString("app_theme", "light").apply()
                    }
                    2 -> {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES)
                        prefs.edit().putString("app_theme", "cyber").apply()
                        android.widget.Toast.makeText(this, "Cyber Mode Activated!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .show()
    }
}
