package com.esa.graffai.ui.presentation.home

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.esa.graffai.utils.FileUtils
import com.esa.graffai.viewmodel.MarkerViewModel
import com.esa.graffai.viewmodel.RiwayatViewModel
import com.esa.graffai.viewmodel.RoboflowViewModel
import com.google.android.gms.location.LocationServices

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

    val markerViewModel : MarkerViewModel = hiltViewModel()

    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
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

    val locationFinePermissionAlreadyGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    val locationCoarsePermissionAlreadyGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    val permissionsLocation = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
        )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMap ->
        val permissionGranted = permissionMap.values.reduce{acc, isPermissionranted ->
            acc && isPermissionranted
        }

        if (!permissionGranted) {
            Toast.makeText(context, "Permission location denied", Toast.LENGTH_SHORT).show()
        }
    }

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    LaunchedEffect(Unit) {
        if (!locationFinePermissionAlreadyGranted || !locationCoarsePermissionAlreadyGranted) {
            locationPermissionLauncher.launch(permissionsLocation)
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

        if (selectedImageUri == null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.Gray)
                    .clip(shape = RoundedCornerShape(5.dp))
            ) {
                Text(
                    text = "Belum ada gambar"
                )
            }
        } else {
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(5.dp))
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

                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                            loc?.let {
                                markerViewModel.addMarker(
                                    id = "",
                                    lat = it.latitude,
                                    lng = it.longitude,
                                    label = bestPrediction?.key ?: "Unknown",
                                    confidences = bestPrediction?.value?.confidence?.toFloat() ?: 0f,
                                    imageUrl = uri.toString()
                                )
                            }
                        }
                    }
                ) {
                    Text("Tambahkan Ke Riwayat")
                }
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

        Button(
            onClick = {
                navController.navigate("maps")
            }
        ) {
            Text("Maps")
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
