package com.esa.graffai.ui.presentation.maps

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.esa.graffai.viewmodel.MarkerViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun Maps(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val markerViewModel : MarkerViewModel = hiltViewModel()
    val marker by markerViewModel.marker.collectAsState()

    LaunchedEffect(Unit) {
        markerViewModel.getAllMarkers()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView(
            factory = {
                MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(-6.879704, 109.125595))
                }
            },
            update = { mapView ->
                mapView.overlays.clear()

                val usedPositions = mutableSetOf<Pair<Double, Double>>()

                marker.forEach { markerModel ->
                    var lat = markerModel.lat
                    var lng = markerModel.lng

                    while (usedPositions.contains(Pair(lat, lng))) {
                        lat += 0.00005
                        lng += 0.00005
                    }

                    usedPositions.add(Pair(lat, lng))

                    val geoPoint = GeoPoint(lat, lng)
                    val markerItem = Marker(mapView).apply {
                        position = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Prediksi: ${markerModel.label}"
                        snippet = "Confidence: ${"%.2f".format(markerModel.confidences * 100)}%"

                        setOnMarkerClickListener{_, _ ->
                            markerViewModel.deleteMarker(markerModel = markerModel)
                            Toast.makeText(context, "marker berhasil di hapus", Toast.LENGTH_SHORT).show()
                            true
                        }
                    }
                    mapView.overlays.add(markerItem)
                }
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
