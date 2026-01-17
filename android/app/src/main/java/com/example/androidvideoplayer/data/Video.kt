package com.example.androidvideoplayer.data

import android.net.Uri

data class Video(
    val id: Long,
    val name: String,
    val path: String,
    val duration: Long,
    val size: Long,
    val dateAdded: Long,
    val uri: Uri
)
