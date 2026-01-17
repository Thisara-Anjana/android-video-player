package com.example.androidvideoplayer.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.androidvideoplayer.data.model.Video
import com.example.androidvideoplayer.data.model.VideoFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class VideoRepository(private val context: Context) {

    suspend fun getVideos(): List<Video> = withContext(Dispatchers.IO) {
        val videoList = mutableListOf<Video>()
        val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA, // For path to get folder name if needed, though BUCKET_DISPLAY_NAME is better
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        try {
            val cursor: Cursor? = context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                sortOrder
            )

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getString(nameColumn)
                    val duration = it.getLong(durationColumn)
                    val size = it.getLong(sizeColumn)
                    val dateAdded = it.getLong(dateAddedColumn)
                    val folderName = it.getString(bucketNameColumn) ?: "Internal Storage"

                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    videoList.add(
                        Video(
                            id = id,
                            uri = contentUri,
                            name = name,
                            duration = duration,
                            size = size,
                            dateAdded = dateAdded,
                            folderName = folderName
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        videoList
    }

    suspend fun getVideoFolders(): List<VideoFolder> = withContext(Dispatchers.IO) {
        val videos = getVideos()
        videos.groupBy { it.folderName }.map { (folderName, videoList) ->
            VideoFolder(
                id = folderName.hashCode().toString(),
                name = folderName,
                videos = videoList
            )
        }
    }

    // Database Integration
    private val database = com.example.androidvideoplayer.data.database.AppDatabase.getDatabase(context)
    private val playbackDao = database.playbackDao()

    suspend fun savePlaybackPosition(videoId: Long, position: Long) {
        val entity = com.example.androidvideoplayer.data.database.PlaybackEntity(
            videoId = videoId,
            position = position,
            timestamp = System.currentTimeMillis()
        )
        playbackDao.savePlaybackPosition(entity)
    }

    suspend fun getPlaybackPosition(videoId: Long): Long {
        return playbackDao.getPlaybackPosition(videoId)?.position ?: 0L
    }
    
    suspend fun getRecentVideos(): List<Video> = withContext(Dispatchers.IO) {
        val playbackHistory = playbackDao.getRecentVideos(20) // Limit 20
        val allVideos = getVideos().associateBy { it.id }
        
        playbackHistory.mapNotNull { history ->
            allVideos[history.videoId]
        }
    }
}
