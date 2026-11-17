package com.crowncode.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crowncode.presentation.screens.aimusic.AiMusicDetectionScreen
import com.crowncode.presentation.screens.history.HistoryScreen
import com.crowncode.presentation.screens.welcome.WelcomeScreen

@Composable
fun CrownCodeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Welcome,
        modifier = Modifier.fillMaxSize()
    ) {
        composable<Route.Welcome>(
            enterTransition = { fadeIn(tween(500)) },
            exitTransition = { fadeOut(tween(250)) }
        ) {
            WelcomeScreen(
                onStartAnalysis = {
                    navController.navigate(Route.AiMusicDetection) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                },
                onOpenHistory = {
                    navController.navigate(Route.History)
                }
            )
        }

        composable<Route.AiMusicDetection>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            AiMusicDetectionScreen(
                onNavigateToHistory = {
                    navController.navigate(Route.History)
                }
            )
        }

        composable<Route.History>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
