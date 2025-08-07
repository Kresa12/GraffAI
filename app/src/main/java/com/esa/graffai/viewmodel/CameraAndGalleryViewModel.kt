package com.esa.graffai.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import coil3.Bitmap
import com.esa.graffai.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CameraAndGalleryViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri : StateFlow<Uri?> = _selectedImageUri

    fun handleGalleryResult(uri: Uri?) {
        uri?.let {
            val bitmap = FileUtils.uriToBitmap(context, uri)
            bitmap?.let {
                val savedUri = FileUtils.saveBitmapToInternalStorage(context, it)
                _selectedImageUri.value = savedUri
            }
        }
    }

    fun handleCameraResult(bitmap : Bitmap?) {
        bitmap?.let {
            val savedUri = FileUtils.saveBitmapToInternalStorage(context, bitmap)
            _selectedImageUri.value = savedUri
        }
    }

    fun resetImage() {
        _selectedImageUri.value = null
    }
}