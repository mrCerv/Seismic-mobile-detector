package com.example.seismicdetector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detections")
data class DetectionEntity(
    @PrimaryKey(autoGenerate = true) 
    val id: Long = 0,
    val timestamp: Long,
    val confidence: Float,
    val intensity: Int,
    val intensityLabel: String,
    val pga: Float,
    val dominantFreq: Float,
    val duration: Float,
    val waveformPath: String? = null // Path to saved CSV file if any
)
