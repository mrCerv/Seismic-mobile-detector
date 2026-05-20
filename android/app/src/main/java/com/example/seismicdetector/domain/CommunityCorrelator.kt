package com.example.seismicdetector.domain

import com.example.seismicdetector.data.FirestoreEvent
import com.example.seismicdetector.data.NetworkRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class NetworkConfirmedEvent(
    val localEvent: DetectionResult.EarthquakeDetected,
    val confirmedByDevices: Int,
    val networkConfidence: Float
)

@Singleton
class CommunityCorrelator @Inject constructor(
    private val networkRepository: NetworkRepository
) {
    private val _confirmedEventFlow = MutableSharedFlow<NetworkConfirmedEvent>()
    val confirmedEventFlow = _confirmedEventFlow.asSharedFlow()

    // Called when local ML detects an event; checks if other devices near the same location also detected
    suspend fun correlateWithNetwork(
        localEvent: DetectionResult.EarthquakeDetected,
        latApprox: Double,
        lonApprox: Double
    ) {
        val recentNearby = networkRepository.getRecentEventsNearby(
            latApprox = latApprox,
            lonApprox = lonApprox,
            radiusKm = 50.0,
            hoursBack = 1
        )

        // Filter events within 5 minutes of our detection
        val windowMs = 5 * 60 * 1000L
        val correlatedEvents = recentNearby.filter { event ->
            kotlin.math.abs(event.timestamp - localEvent.timestamp) < windowMs
                    && event.confidence >= 0.6f
        }

        if (correlatedEvents.size >= 1) {
            val avgNetworkConfidence = correlatedEvents.map { it.confidence }.average().toFloat()
            _confirmedEventFlow.emit(
                NetworkConfirmedEvent(
                    localEvent = localEvent,
                    confirmedByDevices = correlatedEvents.size + 1,
                    networkConfidence = (localEvent.confidence + avgNetworkConfidence) / 2
                )
            )
        }
    }
}
