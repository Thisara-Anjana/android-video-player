package com.example.androidvideoplayer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.androidvideoplayer.data.model.Video
import com.example.androidvideoplayer.data.model.VideoFolder
import com.example.androidvideoplayer.data.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application)

    private val _videoFolders = MutableLiveData<List<VideoFolder>>()
    val videoFolders: LiveData<List<VideoFolder>> = _videoFolders

    // Audio Data
    data class AudioFile(val id: String, val title: String, val artist: String, val path: String, val duration: Long)
    private val _audioFiles = MutableLiveData<List<AudioFile>>()
    val audioFiles: LiveData<List<AudioFile>> = _audioFiles

    private val _allVideos = MutableLiveData<List<Video>>() // Flattened list for search or simple view
    val allVideos: LiveData<List<Video>> = _allVideos
    
    // For navigation: specific folder videos
    private val _currentFolderVideos = MutableLiveData<List<Video>>()
    val currentFolderVideos: LiveData<List<Video>> = _currentFolderVideos
    
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVideos() {
        viewModelScope.launch {
            _isLoading.value = true
            loadAudio() // Load audio concurrently
            
            val folders = repository.getVideoFolders()
            _videoFolders.value = folders
            
            val videos = repository.getVideos()
            _allVideos.value = videos
            _isLoading.value = false
        }
    }
    
    private fun loadAudio() {
        viewModelScope.launch(Dispatchers.IO) {
            val audioProjection = arrayOf(
                android.provider.MediaStore.Audio.Media._ID,
                android.provider.MediaStore.Audio.Media.TITLE,
                android.provider.MediaStore.Audio.Media.ARTIST,
                android.provider.MediaStore.Audio.Media.DATA,
                android.provider.MediaStore.Audio.Media.DURATION
            )

            val cursor = getApplication<Application>().contentResolver.query(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                audioProjection,
                "${android.provider.MediaStore.Audio.Media.IS_MUSIC} != 0",
                null,
                "${android.provider.MediaStore.Audio.Media.TITLE} ASC"
            )

            val audios = mutableListOf<AudioFile>()

            cursor?.use {
                val idCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media._ID)
                val titleCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.TITLE)
                val artistCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.ARTIST)
                val pathCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DATA)
                val durationCol = it.getColumnIndexOrThrow(android.provider.MediaStore.Audio.Media.DURATION)

                while (it.moveToNext()) {
                    val id = it.getString(idCol)
                    val title = it.getString(titleCol)
                    val artist = it.getString(artistCol)
                    val path = it.getString(pathCol)
                    val duration = it.getLong(durationCol)

                    audios.add(AudioFile(id, title, artist, path, duration))
                }
            }
            _audioFiles.postValue(audios)
        }
    }
    
    fun openFolder(folder: VideoFolder) {
        _currentFolderVideos.value = folder.videos
    }

    fun searchVideos(query: String) {
        val currentList = _allVideos.value ?: return
        if (query.isBlank()) {
            loadVideos()
            return
        }
        
        val filtered = currentList.filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        _allVideos.value = filtered
    }
    
    fun saveProgress(videoId: Long, position: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.savePlaybackPosition(videoId, position)
        }
    }
    
    suspend fun getPlaybackPosition(videoId: Long): Long {
        return repository.getPlaybackPosition(videoId)
    }
    
    private val _recentVideos = MutableLiveData<List<Video>>()
    val recentVideos: LiveData<List<Video>> = _recentVideos

    fun loadRecentVideos() {
         viewModelScope.launch(Dispatchers.IO) {
             val videos = repository.getRecentVideos()
             _recentVideos.postValue(videos)
         }
    }
}
