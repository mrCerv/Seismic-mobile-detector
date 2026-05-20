package com.example.seismicdetector.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val events by viewModel.nearbyEvents.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "${events.size} eventi nelle ultime 24h",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { context ->
                    Configuration.getInstance().userAgentValue = context.packageName
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(7.0)
                        val center = userLocation ?: GeoPoint(42.0, 12.0) // Default: Italy
                        controller.setCenter(center)
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    events.forEach { event ->
                        val marker = Marker(mapView).apply {
                            position = GeoPoint(event.latApprox, event.lonApprox)
                            title = "${event.intensityLabel} — PGA: ${"%.2f".format(event.pga)} m/s²"
                            snippet = "Confidence: ${"%.0f".format(event.confidence * 100)}%"
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            if (events.isEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp)
                ) {
                    Text(
                        "Nessun evento nelle ultime 24h\nnell'area selezionata",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
