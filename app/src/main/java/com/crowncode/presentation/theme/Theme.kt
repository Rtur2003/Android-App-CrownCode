package com.crowncode.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CrownCodeColorScheme = darkColorScheme(
    // Primary
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = Secondary,
    onPrimaryContainer = TextPrimary,

    // Secondary
    secondary = Secondary,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceAlt,
    onSecondaryContainer = TextPrimary,

    // Tertiary
    tertiary = Accent,
    onTertiary = TextOnPrimary,
    tertiaryContainer = SurfaceElevated,
    onTertiaryContainer = TextPrimary,

    // Background & Surface
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHighest = SurfaceElevated,

    // Outline
    outline = Border,
    outlineVariant = BorderLight,

    // Status
    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error
)

@Composable
fun CrownCodeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = CrownCodeColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
