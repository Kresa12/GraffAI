package com.esa.graffai.ui.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.esa.graffai.R

@Composable
fun OnBoarding(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Gambar ilustrasi di atas
        Image(
            painter = painterResource(id = R.drawable.ic_onboarding), // Ganti sesuai drawable kamu
            contentDescription = "Onboarding Illustration",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // biar lebih proporsional
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Judul & Deskripsi
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Selamat Datang di GraffAI",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Aplikasi untuk mengenali dan memetakan graffiti di sekitarmu menggunakan teknologi AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Mulai
        Button(
            onClick = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true } // Supaya onboarding tidak bisa balik lagi
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mulai Sekarang")
        }
    }
}
