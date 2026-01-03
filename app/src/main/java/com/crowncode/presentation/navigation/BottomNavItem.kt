package com.crowncode.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AudioFile
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: Route,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(
        route = Route.Home,
        label = "Ana Sayfa",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    AI_MUSIC(
        route = Route.AiMusicDetection,
        label = "AI Müzik",
        selectedIcon = Icons.Filled.AudioFile,
        unselectedIcon = Icons.Outlined.AudioFile
    ),
    ML_TOOLKIT(
        route = Route.MlToolkit,
        label = "ML Toolkit",
        selectedIcon = Icons.Filled.Build,
        unselectedIcon = Icons.Outlined.Build
    ),
    SEARCH(
        route = Route.Search,
        label = "Arama",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search
    )
}
