package com.esa.graffai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.model.MarkerModel
import com.esa.graffai.data.repository.MarkerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarkerViewModel @Inject constructor(
    private val repository: MarkerRepository
) : ViewModel() {
    private val _markers = MutableStateFlow<List<MarkerModel>>(emptyList())
    val marker : StateFlow<List<MarkerModel>> = _markers

    fun getAllMarkers() {
        viewModelScope.launch {
            val result = repository.getAllMarkers()
            _markers.value = result
        }
    }

    fun addMarker(id : String, lat : Double, lng : Double, label : String ,confidences : Float, imageUrl : String) {
        viewModelScope.launch {
            val marker = MarkerModel(
                id = id,
                lat = lat,
                lng = lng,
                label = label,
                confidences = confidences,
                imageUrl = imageUrl
            )
            repository.addMarker(marker = marker)
            getAllMarkers()
        }
    }

    fun deleteMarker(markerModel: MarkerModel) {
        viewModelScope.launch {
            repository.deleteMarker(marker = markerModel)
            getAllMarkers()
        }
    }
}