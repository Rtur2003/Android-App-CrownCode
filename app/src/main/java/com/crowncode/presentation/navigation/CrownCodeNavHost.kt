package com.crowncode.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crowncode.presentation.screens.aimusic.AiMusicDetectionScreen

@Composable
fun CrownCodeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.AiMusicDetection,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<Route.AiMusicDetection> {
            AiMusicDetectionScreen()
        }
    }
}
