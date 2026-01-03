package com.crowncode.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.Surface

@Composable
fun BackgroundWithGlow(
    modifier: Modifier = Modifier,
    glowColor: Color = Primary,
    glowAlpha: Float = 0.15f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Surface.copy(alpha = 0.8f),
                            Background,
                            Background
                        )
                    )
                )
        )

        // Decorative glow effect - top left
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-100).dp)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    )
                )
        )

        // Decorative glow effect - bottom right
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = 200.dp, y = 600.dp)
                .blur(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha * 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        content()
    }
}
