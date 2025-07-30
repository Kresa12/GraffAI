package com.esa.graffai.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

object FileUtils {

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uriToMultipart(context: Context, uri: Uri, paramName: String = "file"): MultipartBody.Part? {
        return try {
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: return null
            val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(paramName, fileName, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
