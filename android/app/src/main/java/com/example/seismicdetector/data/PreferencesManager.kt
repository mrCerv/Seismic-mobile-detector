package com.example.seismicdetector.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(
    val detectionThreshold: Float = 0.7f,
    val minPga: Float = 0.05f,
    val enableNotifications: Boolean = true,
    val enableVibration: Boolean = true,
    val enableSound: Boolean = true,
    val recordWaveform: Boolean = true,
    val shareDataConsent: Boolean = false,   // share detections to Firestore
    val enablePWaveAlert: Boolean = true,    // enable P-wave early detection
    val staSltaThreshold: Float = 3.0f       // STA/LTA trigger threshold
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
            recordWaveform = prefs.getBoolean("record_waveform", true),
            shareDataConsent = prefs.getBoolean("share_data_consent", false),
            enablePWaveAlert = prefs.getBoolean("enable_p_wave", true),
            staSltaThreshold = prefs.getFloat("sta_lta_threshold", 3.0f)
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
            putBoolean("share_data_consent", newSettings.shareDataConsent)
            putBoolean("enable_p_wave", newSettings.enablePWaveAlert)
            putFloat("sta_lta_threshold", newSettings.staSltaThreshold)
            apply()
        }
        _settings.value = newSettings
    }

    fun updateThreshold(value: Float) {
        updateSettings(_settings.value.copy(detectionThreshold = value))
    }

    fun hasAcceptedDisclaimer(): Boolean {
        return prefs.getBoolean("disclaimer_accepted", false)
    }

    fun setDisclaimerAccepted() {
        prefs.edit().putBoolean("disclaimer_accepted", true).apply()
    }

    fun getOrCreateAnonymousId(): String {
        val stored = prefs.getString("anonymous_id", null)
        if (stored != null) return stored

        val rawId = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(rawId.toByteArray(Charsets.UTF_8))
        val hashed = hashBytes.joinToString("") { "%02x".format(it) }

        prefs.edit().putString("anonymous_id", hashed).apply()
        return hashed
    }
}
