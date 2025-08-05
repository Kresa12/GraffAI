package com.esa.graffai.ui.presentation.maps

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
                val mapView = MapView(context).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(-6.879704, 109.125595))
                }
                mapView
            },
            update = { mapView ->
                mapView.overlays.clear()
                marker.forEach {
                    val geoPoint = GeoPoint(it.lat, it.lng)
                    val markerItem = Marker(mapView).apply {
                        position = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Prediksi : ${it.label}"
                        snippet = "Confidence: ${"%.2f".format(it.confidences * 100)}%"
                    }
                    mapView.overlays.add(markerItem)
                }
                mapView.invalidate()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}