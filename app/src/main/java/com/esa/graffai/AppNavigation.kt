package com.esa.graffai

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.esa.graffai.ui.presentation.home.Home
import com.esa.graffai.ui.presentation.riwayat.Riwayat

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            Home(
                navController = navController,
                modifier = modifier
            )
        }

        composable("riwayat") {
            Riwayat(
                modifier = modifier
            )
        }
    }
}