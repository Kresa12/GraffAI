package com.esa.graffai.ui.presentation.home

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.esa.graffai.data.RoboflowViewModel

@Composable
fun Home(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModelLaunchGalleryAndCameraViewModel : LaunchGalleryAndCameraViewModel = hiltViewModel()
    val viewModelRoboflow : RoboflowViewModel = hiltViewModel()
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
            val uri = viewModelLaunchGalleryAndCameraViewModel.saveBitmapToCache(context, bitmap)
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
                if (viewModelLaunchGalleryAndCameraViewModel.hasPermissions(context, permissions)) {
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
                        viewModelLaunchGalleryAndCameraViewModel.sendImageAPI(context, it){ success ->
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


