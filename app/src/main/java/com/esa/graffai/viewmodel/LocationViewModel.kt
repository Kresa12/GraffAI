package com.esa.graffai.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.esa.graffai.utils.DEFAULT_LAT
import com.esa.graffai.utils.DEFAULT_LNG
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    //masih ada masalah, kalau user menolak permission location, permission tidak akan muncul lagi
    //besok lanjut flow untuk lokasi ya agar uxnya enak
    //bikin bottom navigation???
    //marker belum muncul
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun isLocationPermissionGranted(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun getLastUserLocation(
        onGetLastLocationSuccess: (Double, Double) -> Unit,
        onGetLastLocationFailed: () -> Unit
    ) {
        if (isLocationPermissionGranted()) {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            onGetLastLocationSuccess(loc.latitude, loc.longitude)
                        } else {
                            onGetLastLocationSuccess(DEFAULT_LAT, DEFAULT_LNG)
                            onGetLastLocationFailed()
                        }
                    }
                    .addOnFailureListener {
                        onGetLastLocationSuccess(DEFAULT_LAT, DEFAULT_LNG)
                        onGetLastLocationFailed()
                    }
            } catch (e: SecurityException) {
                onGetLastLocationSuccess(DEFAULT_LAT, DEFAULT_LNG)
                onGetLastLocationFailed()
            }
        } else {
            onGetLastLocationSuccess(DEFAULT_LAT, DEFAULT_LNG)
        }
    }
}