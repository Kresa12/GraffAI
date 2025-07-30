package com.esa.graffai.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esa.graffai.data.repository.RoboflowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RoboflowViewModel @Inject constructor(
    private val repository: RoboflowRepository
) : ViewModel() {

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    fun classifyImage(imageFile: File) {
        viewModelScope.launch {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val multipart = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            try {
                val response = repository.classyfyImage(multipart)
                _result.value = response.toString()
            } catch (e: Exception) {
                _result.value = "Error: ${e.message}"
            }
        }
    }
}