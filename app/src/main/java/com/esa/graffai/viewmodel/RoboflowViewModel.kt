package com.esa.graffai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.model.PredictedResponeseModel
import com.esa.graffai.data.repository.RoboflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoboflowViewModel @Inject constructor(
    private val repository: RoboflowRepository
) : ViewModel() {

    private val _result = MutableLiveData<PredictedResponeseModel>()
    val result: LiveData<PredictedResponeseModel> = _result

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun classifyImageFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val multipart = com.esa.graffai.utils.FileUtils.uriToMultipart(context, uri)
            if (multipart != null) {
                try {
                    val response = repository.classyfyImage(multipart)
                    _result.value = response
                    _error.value = null
                } catch (e: Exception) {
                    _error.value = "Error: ${e.message}"
                }
            } else {
                _error.value = "Gagal membaca gambar dari URI"
            }
        }
    }
}
