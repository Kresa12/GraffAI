package com.esa.graffai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.repository.RoboflowRepository
import com.esa.graffai.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoboflowViewModel @Inject constructor(
    private val repository: RoboflowRepository
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    fun classifyImageFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val multipart = FileUtils.uriToMultipart(context, uri)
            if (multipart != null) {
                try {
                    val response = repository.classyfyImage(multipart)
                    _result.value = response.toString()
                } catch (e: Exception) {
                    _result.value = "Error: ${e.message}"
                }
            } else {
                _result.value = "Gagal membaca gambar dari URI"
            }
        }
    }

}