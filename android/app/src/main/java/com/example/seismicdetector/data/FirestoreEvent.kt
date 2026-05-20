package com.example.seismicdetector.data

data class FirestoreEvent(
    val eventId: String = "",
    val anonymousDeviceId: String = "",
    val timestamp: Long = 0L,
    val confidence: Float = 0f,
    val intensity: Int = 0,
    val intensityLabel: String = "",
    val pga: Float = 0f,
    val dominantFreq: Float = 0f,
    val duration: Float = 0f,
    val latApprox: Double = 0.0,
    val lonApprox: Double = 0.0,
    val appVersion: String = "1.0.0"
)
