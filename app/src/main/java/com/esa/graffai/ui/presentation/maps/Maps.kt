package com.esa.graffai.ui.presentation.maps

import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun Maps(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AndroidView(
            factory = {
                val mapView = MapView(it)
                Configuration.getInstance().load(it, PreferenceManager.getDefaultSharedPreferences(it))
                mapView.setTileSource(TileSourceFactory.MAPNIK)
                mapView.setBuiltInZoomControls(true)
                mapView.setMultiTouchControls(true)

                val mapController = mapView.controller
                mapController.setZoom(15.0)

                val startPoint = GeoPoint(-6.52, 109.8)
                mapController.setCenter(startPoint)

                mapView
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}