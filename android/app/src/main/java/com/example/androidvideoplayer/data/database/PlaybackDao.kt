package com.example.androidvideoplayer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaybackDao {
    @Query("SELECT * FROM playback_history WHERE videoId = :videoId LIMIT 1")
    suspend fun getPlaybackPosition(videoId: Long): PlaybackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlaybackPosition(entity: PlaybackEntity)

    @Query("SELECT * FROM playback_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentVideos(limit: Int): List<PlaybackEntity>
}
