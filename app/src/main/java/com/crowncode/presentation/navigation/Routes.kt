package com.crowncode.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Welcome : Route

    @Serializable
    data object AiMusicDetection : Route

    @Serializable
    data object History : Route
}
