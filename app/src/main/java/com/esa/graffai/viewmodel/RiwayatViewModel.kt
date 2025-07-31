package com.esa.graffai.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.local.riwayat.RiwayatEntity
import com.esa.graffai.data.repository.RiwayatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val repository: RiwayatRepository
) : ViewModel(){

    val getALlRiwayat : StateFlow<List<RiwayatEntity>> =
        repository.getAllRiwayat()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insert(imageUri: Uri, predictionLabel: String, confidence: Float) {
        viewModelScope.launch {
            val riwayat = RiwayatEntity(
                imageUri = imageUri.toString(),
                predictionLabel = predictionLabel,
                confidence = confidence
            )
            repository.insertRiwayat(riwayat)
        }
    }

    fun delete(riwayat: RiwayatEntity) {
        viewModelScope.launch {
            repository.deleteRiwayat(riwayat)
        }
    }
}