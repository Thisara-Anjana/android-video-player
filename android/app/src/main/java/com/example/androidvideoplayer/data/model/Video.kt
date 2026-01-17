package com.example.androidvideoplayer.data.model

import android.net.Uri

data class Video(
    val id: Long,
    val uri: Uri,
    val name: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val folderName: String
)
