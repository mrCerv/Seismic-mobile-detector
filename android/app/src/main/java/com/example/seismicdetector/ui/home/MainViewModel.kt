package com.example.seismicdetector.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seismicdetector.data.PreferencesManager
import com.example.seismicdetector.domain.DetectionResult
import com.example.seismicdetector.domain.EarthquakeDetector
import com.example.seismicdetector.domain.SeismicSensorManager
import com.example.seismicdetector.domain.SensorDataBatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isMonitoring: Boolean = false,
    val currentSampleRate: Float = 0f,
    val lastDetection: DetectionResult.EarthquakeDetected? = null,
    val sensorData: SensorDataBatch? = null // For real-time graph
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sensorManager: SeismicSensorManager,
    private val detector: EarthquakeDetector,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Observe settings changes to update detector thresholds
        viewModelScope.launch {
            preferencesManager.settings.collectLatest { settings ->
                detector.confidenceThreshold = settings.detectionThreshold
                detector.minPgaThreshold = settings.minPga
            }
        }

        // Collect Sensor Data for Graph UI
        viewModelScope.launch {
            sensorManager.sensorDataFlow.collect { batch ->
                // Update UI with graphical data
                // Note: updating sample rate here too
                _uiState.value = _uiState.value.copy(
                    sensorData = batch,
                    currentSampleRate = sensorManager.currentSampleRate
                )
            }
        }
        
        // Collect Detections
        viewModelScope.launch {
            detector.detectionFlow.collect { result ->
                if (result is DetectionResult.EarthquakeDetected) {
                    _uiState.value = _uiState.value.copy(lastDetection = result)
                }
            }
        }
        
        refreshMonitoringState()
    }
    
    fun toggleMonitoring() {
        if (sensorManager.isMonitoring) {
            sensorManager.stopMonitoring()
        } else {
            sensorManager.startMonitoring()
        }
        refreshMonitoringState()
    }
    
    private fun refreshMonitoringState() {
        _uiState.value = _uiState.value.copy(isMonitoring = sensorManager.isMonitoring)
    }
}
