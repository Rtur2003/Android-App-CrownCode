package com.crowncode.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Home : Route

    @Serializable
    data object AiMusicDetection : Route

    @Serializable
    data object MlToolkit : Route

    @Serializable
    data object Search : Route
}
