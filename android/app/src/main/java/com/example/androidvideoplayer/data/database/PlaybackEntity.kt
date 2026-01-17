package com.example.androidvideoplayer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_history")
data class PlaybackEntity(
    @PrimaryKey val videoId: Long,
    val position: Long,
    val timestamp: Long // When it was last played
)
