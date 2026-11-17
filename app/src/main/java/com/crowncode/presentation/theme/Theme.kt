package com.crowncode.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CrownCodeColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextOnPrimary,
    primaryContainer = Secondary,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceAlt,
    onSecondaryContainer = TextPrimary,
    tertiary = Accent,
    onTertiary = TextOnPrimary,
    tertiaryContainer = SurfaceElevated,
    onTertiaryContainer = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    surfaceContainerHighest = SurfaceElevated,
    outline = Border,
    outlineVariant = BorderLight,
    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error
)

@Composable
fun CrownCodeTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = CrownCodeColorScheme,
        typography = Typography,
        content = content
    )
}
