package com.crowncode.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.GradientEnd
import com.crowncode.presentation.theme.GradientMiddle
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.PortmanteauFamily
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary

enum class AurisLogoSize(val iconSize: Dp, val fontSize: Int, val showText: Boolean) {
    Large(80.dp, 28, true),
    Medium(56.dp, 20, true),
    Small(40.dp, 14, false),
    Icon(32.dp, 0, false)
}

@Composable
fun AurisLogo(
    modifier: Modifier = Modifier,
    size: AurisLogoSize = AurisLogoSize.Large,
    animated: Boolean = true,
    showSubtitle: Boolean = false
) {
    var isVisible by remember { mutableStateOf(!animated) }

    LaunchedEffect(Unit) {
        if (animated) {
            isVisible = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    // Glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Column(
        modifier = modifier
            .scale(if (animated) scale else 1f)
            .alpha(if (animated) alpha else 1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with glow
        Box(contentAlignment = Alignment.Center) {
            // Glow effect
            if (animated) {
                Box(
                    modifier = Modifier
                        .size(size.iconSize + 20.dp)
                        .blur(20.dp)
                        .alpha(glowAlpha)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.5f),
                                    Primary.copy(alpha = 0f)
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }

            // Main icon container
            Box(
                modifier = Modifier
                    .size(size.iconSize)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.8f),
                                Primary.copy(alpha = 0.3f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Ear icon with sound waves
                EarWithWavesIcon(
                    modifier = Modifier.size(size.iconSize * 0.6f)
                )
            }
        }

        if (size.showText) {
            Spacer(modifier = Modifier.height(12.dp))

            // AURIS text
            Text(
                text = "AURIS",
                fontFamily = PortmanteauFamily,
                fontSize = size.fontSize.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                letterSpacing = 4.sp
            )

            if (showSubtitle) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "AI Music Detection",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
private fun EarWithWavesIcon(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waves")

    val wave1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )

    val wave2Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerX = width * 0.4f
        val centerY = height * 0.5f

        // Draw simplified ear shape
        val earPath = Path().apply {
            moveTo(centerX, centerY - height * 0.35f)
            cubicTo(
                centerX + width * 0.3f, centerY - height * 0.3f,
                centerX + width * 0.35f, centerY + height * 0.1f,
                centerX + width * 0.1f, centerY + height * 0.35f
            )
            cubicTo(
                centerX - width * 0.1f, centerY + height * 0.4f,
                centerX - width * 0.2f, centerY + height * 0.2f,
                centerX - width * 0.1f, centerY
            )
            cubicTo(
                centerX, centerY - height * 0.15f,
                centerX + width * 0.1f, centerY - height * 0.2f,
                centerX, centerY - height * 0.35f
            )
        }

        drawPath(
            path = earPath,
            color = Background,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // Sound wave arcs
        val waveStartX = centerX + width * 0.25f

        // Wave 1
        drawArc(
            color = Background.copy(alpha = wave1Alpha),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(waveStartX, centerY - height * 0.15f),
            size = androidx.compose.ui.geometry.Size(width * 0.2f, height * 0.3f),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )

        // Wave 2
        drawArc(
            color = Background.copy(alpha = wave2Alpha),
            startAngle = -45f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(waveStartX + width * 0.1f, centerY - height * 0.2f),
            size = androidx.compose.ui.geometry.Size(width * 0.25f, height * 0.4f),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AurisLogoWithTagline(
    modifier: Modifier = Modifier,
    size: AurisLogoSize = AurisLogoSize.Large
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AurisLogo(size = size, showSubtitle = true)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0f),
                                Primary.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
            Text(
                text = "by CrownCode",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary.copy(alpha = 0.7f)
            )
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.5f),
                                Primary.copy(alpha = 0f)
                            )
                        )
                    )
            )
        }
    }
}
