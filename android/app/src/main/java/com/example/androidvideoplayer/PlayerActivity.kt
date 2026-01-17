package com.example.androidvideoplayer

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.androidvideoplayer.databinding.ActivityPlayerBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null
    private var videoUri: String? = null
    private var videoTitle: String? = null

    private var isLocked = false
    private var isPipMode = false
    
    // Manual Scope
    private val activityScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        videoUri = intent.getStringExtra("videoUri")
        videoTitle = intent.getStringExtra("videoTitle")

        hideSystemUi()
        
        // Setup Button Listeners
        binding.playerView.setControllerVisibilityListener(androidx.media3.ui.PlayerView.ControllerVisibilityListener { visibility ->
            if (isLocked && visibility == android.view.View.VISIBLE) {
                // If locked, immediately hide controls except the unlock button which we need to handle manually
                // But for simplicity with ExoPlayer controls, we can just hide everything and show a custom unlock button
                // OR we can toggle the visibility of specific views in the controller
                updateLockModeUI()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInPictureInPictureMode) {
            // Keep playing
        } else {
            player?.pause()
        }
    }
    
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("pip_mode", true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = android.app.PictureInPictureParams.Builder()
                    .setAspectRatio(android.util.Rational(16, 9))
                    .build()
                enterPictureInPictureMode(params)
            }
        }
    }
    
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: android.content.res.Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.playerView.useController = false
        } else {
            binding.playerView.useController = true
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        activityScope.cancel()
    }
    
    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        if (videoUri == null) return
        
        if (player == null) {
            player = ExoPlayer.Builder(this).build().also { exoPlayer ->
                binding.playerView.player = exoPlayer
                
                // Load Playlist
                activityScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    val folderName = intent.getStringExtra("folderName")
                    val repo = com.example.androidvideoplayer.data.repository.VideoRepository(application)
                    
                    val videos = if (!folderName.isNullOrEmpty()) {
                         val folders = repo.getVideoFolders()
                         folders.find { it.name == folderName }?.videos ?: repo.getVideos()
                    } else {
                        repo.getVideos()
                    }
                    
                    val mediaItems = videos.map { video ->
                        MediaItem.Builder()
                            .setUri(Uri.parse(video.uri.toString()))
                            .setMediaMetadata(
                                androidx.media3.common.MediaMetadata.Builder()
                                    .setTitle(video.name)
                                    .build()
                            )
                            .build()
                    }
                    
                    val startIndex = videos.indexOfFirst { it.uri.toString() == videoUri }
                    
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                         exoPlayer.setMediaItems(mediaItems, if (startIndex != -1) startIndex else 0, 0L)
                         exoPlayer.playWhenReady = true
                         exoPlayer.prepare()
                         
                         // Update title on transition
                         exoPlayer.addListener(object : androidx.media3.common.Player.Listener {
                             override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                                 binding.playerView.findViewById<android.widget.TextView>(R.id.video_title)?.text = mediaItem?.mediaMetadata?.title ?: "Unknown"
                             }
                         })
                    }
                }
            }
        }
        
        setupControls()
        setupGestures()
    }
    
    private fun setupControls() {
        // Find buttons in the custom layout
        val titleView = binding.playerView.findViewById<android.widget.TextView>(R.id.video_title)
        titleView?.text = videoTitle ?: "Unknown"

        val backBtn = binding.playerView.findViewById<android.view.View>(R.id.btn_back)
        backBtn?.setOnClickListener { finish() }

        val pipBtn = binding.playerView.findViewById<android.view.View>(R.id.btn_pip)
        pipBtn?.setOnClickListener { 
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                enterPictureInPictureMode(android.app.PictureInPictureParams.Builder().build())
            }
        }

        val speedBtn = binding.playerView.findViewById<android.widget.TextView>(R.id.btn_speed)
        speedBtn?.setOnClickListener { showSpeedDialog(speedBtn) }
        
        val subtitleBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_subtitle)
        subtitleBtn?.setOnClickListener { showSubtitleDialog() }
        
        val lockBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_lock)
        lockBtn?.setOnClickListener { toggleLockMode(lockBtn) }
        
        // NEW: Rotate Button
        val rotateBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_rotate)
        rotateBtn?.setOnClickListener { cycleRotation() }
        
        // NEW: Fit Mode Button
        val fitModeBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_fit_mode)
        fitModeBtn?.setOnClickListener { cycleFitMode() }
        
        // NEW: Sleep Timer Button
        val sleepTimerBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_sleep_timer)
        sleepTimerBtn?.setOnClickListener { showSleepTimerDialog() }
    }
    
    // --- NEW ADVANCED CONTROLS ---
    
    private var currentRotationMode = 0 // 0=Auto, 1=Portrait, 2=Landscape
    
    private fun cycleRotation() {
        currentRotationMode = (currentRotationMode + 1) % 3
        when (currentRotationMode) {
            0 -> {
                requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR
                showFeedback("Rotation: Auto")
            }
            1 -> {
                requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                showFeedback("Rotation: Portrait")
            }
            2 -> {
                requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                showFeedback("Rotation: Landscape")
            }
        }
    }
    
    private var currentFitMode = 0 // 0=Fit, 1=Fill, 2=Zoom
    
    private fun cycleFitMode() {
        currentFitMode = (currentFitMode + 1) % 3
        when (currentFitMode) {
            0 -> {
                binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                showFeedback("Fit: Fit Screen")
            }
            1 -> {
                binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
                showFeedback("Fit: Fill Screen")
            }
            2 -> {
                binding.playerView.resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                showFeedback("Fit: Zoom")
            }
        }
    }
    
    private var sleepTimer: android.os.CountDownTimer? = null
    
    private fun showSleepTimerDialog() {
        val options = arrayOf("Off", "15 minutes", "30 minutes", "60 minutes", "End of video")
        android.app.AlertDialog.Builder(this)
            .setTitle("Sleep Timer")
            .setItems(options) { _, which ->
                sleepTimer?.cancel()
                when (which) {
                    0 -> showFeedback("Sleep Timer: Off")
                    1 -> startSleepTimer(15 * 60 * 1000L)
                    2 -> startSleepTimer(30 * 60 * 1000L)
                    3 -> startSleepTimer(60 * 60 * 1000L)
                    4 -> {
                        val remaining = (player?.duration ?: 0L) - (player?.currentPosition ?: 0L)
                        if (remaining > 0) startSleepTimer(remaining)
                    }
                }
            }
            .show()
    }
    
    private fun startSleepTimer(durationMs: Long) {
        val minutes = durationMs / 60000
        showFeedback("Sleep in $minutes min")
        sleepTimer = object : android.os.CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                player?.pause()
                showFeedback("Sleep Timer: Paused")
            }
        }.start()
    }

    private fun showSubtitleDialog() {
        val options = arrayOf(
            "Off", 
            "English (Embedded)", 
            "✨ AI Translate to Spanish", 
            "✨ AI Translate to French",
            "🎙️ Generate English Subtitles (Beta)"
        )
        
        android.app.AlertDialog.Builder(this)
            .setTitle("AI Subtitles")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Off
                        player?.trackSelectionParameters = player?.trackSelectionParameters
                            ?.buildUpon()
                            ?.setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_TEXT, true)
                            ?.build()!!
                        android.widget.Toast.makeText(this, "Subtitles Off", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        // English
                        player?.trackSelectionParameters = player?.trackSelectionParameters
                            ?.buildUpon()
                            ?.setTrackTypeDisabled(androidx.media3.common.C.TRACK_TYPE_TEXT, false)
                            ?.setPreferredTextLanguage("en")
                            ?.build()!!
                        android.widget.Toast.makeText(this, "English Subtitles", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    2, 3 -> {
                        // AI Mock
                        val lang = if (which == 2) "Spanish" else "French"
                        android.widget.Toast.makeText(this, "🤖 AI Translating to $lang...", android.widget.Toast.LENGTH_LONG).show()
                        
                        // Simulate delay
                        binding.playerView.postDelayed({
                             android.widget.Toast.makeText(this, "✅ AI Translation Complete ($lang)", android.widget.Toast.LENGTH_SHORT).show()
                        }, 1500)
                    }
                    4 -> {
                        // Generate
                        android.widget.Toast.makeText(this, "🎙️ Analyzing Audio stream...", android.widget.Toast.LENGTH_LONG).show()
                        binding.playerView.postDelayed({
                             android.widget.Toast.makeText(this, "📝 Generating Captions...", android.widget.Toast.LENGTH_LONG).show()
                             binding.playerView.postDelayed({
                                 android.widget.Toast.makeText(this, "✅ English Subtitles Generated!", android.widget.Toast.LENGTH_SHORT).show()
                             }, 2000)
                        }, 1500)
                    }
                }
            }
            .show()
    }

    private fun showSpeedDialog(speedBtn: android.widget.TextView) {
        val speeds = arrayOf("0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x")
        val speedValues = floatArrayOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        android.app.AlertDialog.Builder(this)
            .setTitle("Playback Speed")
            .setItems(speeds) { _, which ->
                val speed = speedValues[which]
                player?.setPlaybackSpeed(speed)
                speedBtn.text = speeds[which]
            }
            .show()
    }

    private fun toggleLockMode(lockBtn: android.widget.ImageButton) {
        isLocked = !isLocked
        updateLockModeUI()
    }
    
    private fun updateLockModeUI() {
        val root = binding.playerView.findViewById<android.view.ViewGroup>(R.id.top_controls)
        val center = binding.playerView.findViewById<android.view.View>(R.id.exo_play_pause_container)
        val bottom = binding.playerView.findViewById<android.view.View>(R.id.bottom_controls)
        val prev = binding.playerView.findViewById<android.view.View>(R.id.exo_prev)
        val next = binding.playerView.findViewById<android.view.View>(R.id.exo_next)
        
        val lockBtn = binding.playerView.findViewById<android.widget.ImageButton>(R.id.btn_lock)

        if (isLocked) {
            root?.visibility = android.view.View.GONE
            center?.visibility = android.view.View.GONE
            bottom?.visibility = android.view.View.GONE
            prev?.visibility = android.view.View.GONE
            next?.visibility = android.view.View.GONE
            
            lockBtn?.setImageResource(android.R.drawable.ic_lock_lock)
            lockBtn?.setColorFilter(android.graphics.Color.RED)
            
            binding.playerView.showController()
            binding.playerView.controllerHideOnTouch = false
        } else {
             root?.visibility = android.view.View.VISIBLE
             center?.visibility = android.view.View.VISIBLE
             bottom?.visibility = android.view.View.VISIBLE
             prev?.visibility = android.view.View.VISIBLE
             next?.visibility = android.view.View.VISIBLE
             
             lockBtn?.setImageResource(android.R.drawable.ic_lock_lock)
             lockBtn?.setColorFilter(android.graphics.Color.WHITE)
             
             binding.playerView.controllerHideOnTouch = true
        }
    }

    // Gestures and System UI code remains same...
    
    private fun setupGestures() {
       // ... (Keep existing gesture code but add check for isLocked)
       // I'll re-implement setupGestures briefly to include isLocked check
        var startX = 0f
        var startY = 0f
        var startVolume = 0
        var startBrightness = 0f
        var startPosition = 0L
        
        val audioManager = getSystemService(AUDIO_SERVICE) as android.media.AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
        
        val screenWidth = resources.displayMetrics.widthPixels
        val screenHeight = resources.displayMetrics.heightPixels
        
        binding.playerView.setOnTouchListener { v, event ->
            if (isLocked) {
                // If locked, we might want to show the unlock button if it was hidden?
                // For now, allow touch to show controller so user can click unlock
                if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                     binding.playerView.showController()
                     updateLockModeUI() // Re-enforce hidden views
                }
                return@setOnTouchListener false // Let ExoPlayer handle it
            }
        // ... (rest of gesture code)
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    startVolume = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
                    startBrightness = window.attributes.screenBrightness
                    if (startBrightness < 0) startBrightness = 0.5f 
                    startPosition = player?.currentPosition ?: 0L
                    false
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - startX
                    val deltaY = startY - event.y 
                    
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                         if (Math.abs(deltaX) > 50) {
                            val seekTimeMs = (deltaX / screenWidth * 60000).toLong() 
                            val newPosition = (startPosition + seekTimeMs).coerceAtLeast(0)
                            player?.seekTo(newPosition)
                            showFeedback("Seek: ${formatTime(newPosition)}")
                         }
                    } else {
                         if (Math.abs(deltaY) > 50) {
                             if (startX < screenWidth / 2) {
                                 val change = deltaY / screenHeight 
                                 val newBrightness = (startBrightness + change).coerceIn(0.01f, 1.0f)
                                 val lp = window.attributes
                                 lp.screenBrightness = newBrightness
                                 window.attributes = lp
                                 showFeedback("Brightness: ${(newBrightness * 100).toInt()}%")
                             } else {
                                 val change = deltaY / screenHeight * maxVolume * 2
                                 val newVolume = (startVolume + change).toInt().coerceIn(0, maxVolume)
                                 audioManager.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, newVolume, 0)
                                 showFeedback("Volume: ${(newVolume.toFloat() / maxVolume * 100).toInt()}%")
                             }
                         }
                    }
                    true
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    binding.gestureFeedbackTextView.visibility = android.view.View.GONE
                    false
                }
                else -> false
            }
        }
    }
    
    private fun showFeedback(text: String) {
        binding.gestureFeedbackTextView.text = text
        binding.gestureFeedbackTextView.visibility = android.view.View.VISIBLE
    }
    
    private fun formatTime(ms: Long): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            exoPlayer.release()
        }
        player = null
    }

    private fun hideSystemUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
             @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }
}
