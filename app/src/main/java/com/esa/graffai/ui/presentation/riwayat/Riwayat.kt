package com.esa.graffai.ui.presentation.riwayat

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.esa.graffai.viewmodel.RiwayatViewModel

@SuppressLint("DefaultLocale")
@Composable
fun Riwayat(
    modifier: Modifier = Modifier
) {
    val viewModelRiwayat: RiwayatViewModel = hiltViewModel()
    val riwayat by viewModelRiwayat.getALlRiwayat.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        riwayat.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(text = item.predictionLabel)
                    Text(text = "Confidence: ${"%.2f".format(item.confidence * 100)}%")
                }
                Button(
                    onClick = {
                        viewModelRiwayat.delete(item)
                    }
                ) {
                    Text("Hapus")
                }
            }
        }
    }
}
