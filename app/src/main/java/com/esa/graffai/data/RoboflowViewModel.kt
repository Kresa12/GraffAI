package com.esa.graffai.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.model.PredictedResponeseModel
import com.esa.graffai.data.repository.RoboflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RoboflowViewModel @Inject constructor(
    private val repository: RoboflowRepository
) : ViewModel() {

    private val _result = MutableStateFlow<PredictedResponeseModel?>(null)
    val result : StateFlow<PredictedResponeseModel?> = _result

    fun classifyImage(file : File){
        viewModelScope.launch {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        }
    }
}