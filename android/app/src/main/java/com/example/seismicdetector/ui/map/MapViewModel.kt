package com.example.seismicdetector.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seismicdetector.data.FirestoreEvent
import com.example.seismicdetector.data.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val _nearbyEvents = MutableStateFlow<List<FirestoreEvent>>(emptyList())
    val nearbyEvents: StateFlow<List<FirestoreEvent>> = _nearbyEvents.asStateFlow()

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation: StateFlow<GeoPoint?> = _userLocation.asStateFlow()

    init {
        loadNearbyEvents()
    }

    private fun loadNearbyEvents() {
        viewModelScope.launch {
            // Default to Italy center when location not available
            val lat = _userLocation.value?.latitude ?: 42.0
            val lon = _userLocation.value?.longitude ?: 12.0
            networkRepository.subscribeToNearbyEvents(lat, lon, 500.0)
                .collect { events -> _nearbyEvents.value = events }
        }
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        _userLocation.value = GeoPoint(lat, lon)
        loadNearbyEvents()
    }
}
