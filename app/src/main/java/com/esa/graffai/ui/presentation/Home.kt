package com.esa.graffai.ui.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showDialog by remember{ mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    selectedImageUri = null

    val launcherGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val uri = saveBitmapToCache(context, bitmap)
            selectedImageUri = uri
        }
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            showDialog = true
        }else {
            Toast.makeText(context, "Permission are required to proceed.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Button(
            onClick = {
                if (hasPermissions(context, permissions)) {
                    showDialog = true
                }else {
                    permissionLauncher.launch(permissions)
                }
            }
        ) {
            Text(
                text = "Pick Image"
            )

            Spacer(Modifier.height(30.dp))

            selectedImageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                )
                var isSending = false
                Button(
                    onClick = {
                        isSending = true
                        sendImageAPI(context, it){ success ->
                            isSending = false
                            if (success) {
                                selectedImageUri = null
                                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isSending
                ) {
                    Text(if (isSending) "Uploading" else "Uploaded")
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Choose an Option") },
                text = { Text(text = "Please select an option to capture or pick an image") },
                confirmButton = {
                    Button(
                        onClick = {
                            launcherGallery.launch("image/*")
                            showDialog = false
                        }
                    ) {
                        Text(
                            text = "Gallery"
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            launcherCamera.launch(null)
                            showDialog = false
                        }
                    ) {
                        Text(
                            text = "Camera"
                        )
                    }
                }
            )
        }
    }
}

fun hasPermissions(context: Context, permissions: Array<String>) : Boolean{
    return permissions.all {permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap) : Uri {
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

fun uriToBase64(context: Context, uri: Uri) : String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }catch (e : Exception){
        e.printStackTrace()
        null
    }
}

fun urlToMultipart(context: Context, uri: Uri, paramName : String = "file") : MultipartBody.Part?{
    return try {
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: return null
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        MultipartBody.Part.createFormData(paramName, fileName, requestBody)
    }catch (e : Exception){
        e.printStackTrace()
        null
    }
}


@OptIn(UnstableApi::class)
fun sendImageAPI(context: Context, uri: Uri, callback : (Boolean) -> Unit){
    val base64String = uriToBase64(context, uri)
    Log.d("Tag_selectedImageUri", "Base64 String: $base64String")

    val multipartBody = urlToMultipart(context, uri)
    Log.d("Tag_selectedImageUri", "Multipart Part: $multipartBody")

    if (multipartBody != null){
        CoroutineScope(Dispatchers.IO).launch {
            delay(2000L)
            withContext(Dispatchers.Main) {
                callback(true)
            }
        }
    }else {
        callback(false)
    }
}
