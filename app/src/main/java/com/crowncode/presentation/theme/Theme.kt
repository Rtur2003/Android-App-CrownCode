package com.crowncode.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = TextOnPrimary,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = SurfaceVariantDark,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = TextPrimary,
    tertiary = GoldLight,
    onTertiary = TextOnPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = GoldDark,
    onPrimary = TextPrimary,
    primaryContainer = GoldLight,
    onPrimaryContainer = TextOnPrimary,
    secondary = SurfaceVariantLight,
    onSecondary = TextOnPrimary,
    secondaryContainer = SurfaceVariantLight,
    onSecondaryContainer = TextOnPrimary,
    tertiary = Gold,
    onTertiary = TextOnPrimary,
    background = BackgroundLight,
    onBackground = TextOnPrimary,
    surface = SurfaceLight,
    onSurface = TextOnPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextOnPrimary,
    error = Error,
    onError = TextPrimary
)

@Composable
fun CrownCodeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
