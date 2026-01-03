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
import com.crowncode.presentation.screens.auth.LoginScreen
import com.crowncode.presentation.screens.auth.SignUpScreen
import com.crowncode.presentation.screens.welcome.WelcomeScreen

@Composable
fun CrownCodeNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Welcome,
        modifier = Modifier.fillMaxSize()
    ) {
        // Welcome Screen
        composable<Route.Welcome>(
            enterTransition = { fadeIn(tween(600)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Route.SignUp) },
                onSignIn = { navController.navigate(Route.Login) },
                onContinueAsGuest = {
                    navController.navigate(Route.AiMusicDetection) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable<Route.Login>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(Route.AiMusicDetection) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Route.SignUp) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }

        // SignUp Screen
        composable<Route.SignUp>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    tween(300)
                )
            }
        ) {
            SignUpScreen(
                onNavigateBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Route.AiMusicDetection) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.SignUp) { inclusive = true }
                    }
                }
            )
        }

        // AI Music Detection Screen
        composable<Route.AiMusicDetection>(
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    tween(300)
                )
            },
            exitTransition = { fadeOut(tween(200)) }
        ) {
            AiMusicDetectionScreen()
        }
    }
}
