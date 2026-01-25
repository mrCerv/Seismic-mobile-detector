package com.example.seismicdetector.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {
    @Query("SELECT * FROM detections ORDER BY timestamp DESC LIMIT 20")
    fun getRecentDetections(): Flow<List<DetectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetection(detection: DetectionEntity)

    @Query("DELETE FROM detections WHERE timestamp < :cutoffTime")
    suspend fun deleteOldDetections(cutoffTime: Long)
    
    @Query("SELECT * FROM detections WHERE id = :id")
    suspend fun getDetectionById(id: Long): DetectionEntity?
}
