package com.example.seismicdetector.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(
    val detectionThreshold: Float = 0.7f,
    val minPga: Float = 0.05f,
    val enableNotifications: Boolean = true,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true,
    val recordWaveform: Boolean = true
)

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("seismic_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            detectionThreshold = prefs.getFloat("detection_threshold", 0.7f),
            minPga = prefs.getFloat("min_pga", 0.05f),
            enableNotifications = prefs.getBoolean("enable_notifications", true),
            enableVibration = prefs.getBoolean("enable_vibration", true),
            enableSound = prefs.getBoolean("enable_sound", true),
            recordWaveform = prefs.getBoolean("record_waveform", true)
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit().apply {
            putFloat("detection_threshold", newSettings.detectionThreshold)
            putFloat("min_pga", newSettings.minPga)
            putBoolean("enable_notifications", newSettings.enableNotifications)
            putBoolean("enable_vibration", newSettings.enableVibration)
            putBoolean("enable_sound", newSettings.enableSound)
            putBoolean("record_waveform", newSettings.recordWaveform)
            apply()
        }
        _settings.value = newSettings
    }
    
    fun updateThreshold(value: Float) {
        updateSettings(_settings.value.copy(detectionThreshold = value))
    }
    
    // Add other granular updates as needed
}
