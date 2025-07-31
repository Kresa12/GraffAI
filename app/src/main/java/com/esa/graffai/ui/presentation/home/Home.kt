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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.esa.graffai.utils.FileUtils
import com.esa.graffai.viewmodel.RiwayatViewModel
import com.esa.graffai.viewmodel.RoboflowViewModel

@Composable
fun Home(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: RoboflowViewModel = hiltViewModel()
    val viewModelRiwayat: RiwayatViewModel = hiltViewModel()

    val result by viewModel.result.observeAsState()
    val error by viewModel.error.observeAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val uriToBitmap = FileUtils.uriToBitmap(context, uri)
            uriToBitmap?.let {bitmap ->
                selectedImageUri = FileUtils.saveBitmapToInternalStorage(context, bitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            selectedImageUri = FileUtils.saveBitmapToInternalStorage(context, it)
        }
    }

    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        if (resultMap.values.all { it }) {
            showDialog = true
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Button(onClick = {
            permissionLauncher.launch(permissions)
        }) {
            Text("Pilih Gambar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                viewModel.classifyImageFromUri(context, uri)
            }) {
                Text("Kirim ke API")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val bestPrediction = result?.predictions?.maxByOrNull { it.value.confidence }
                    val label = bestPrediction?.key ?: "unknown"
                    val confidence = bestPrediction?.value?.confidence?.toFloat() ?: 0f
                    viewModelRiwayat.insert(uri, label, confidence)
                    Toast.makeText(context, "Berhasil ditambahkan ke riwayat", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Tambahkan Ke Riwayat")
            }
        }

        result?.let { prediction ->
            val bestPrediction = prediction.predictions.maxByOrNull { it.value.confidence }
            bestPrediction?.let {
                val confidence = it.value.confidence * 100
                Text("Prediksi: ${it.key}", color = Color.Black)
                Text("Confidence: ${"%.2f".format(confidence)}%", color = Color.Gray)
            }
        }

        error?.let {
            Text("Error: $it", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                navController.navigate("riwayat")
            }
        ) {
            Text("Riwayat")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Pilih Sumber Gambar") },
                text = { Text("Ambil gambar dari mana?") },
                confirmButton = {
                    Button(onClick = {
                        galleryLauncher.launch("image/*")
                        showDialog = false
                    }) {
                        Text("Galeri")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        cameraLauncher.launch(null)
                        showDialog = false
                    }) {
                        Text("Kamera")
                    }
                }
            )
        }
    }
}
