package com.crowncode.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.GradientMiddle
import kotlin.math.sin

@Composable
fun SoundWaveAnimation(
    modifier: Modifier = Modifier,
    waveCount: Int = 3,
    alpha: Float = 0.3f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")

    // Phase animations for each wave
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    val phase3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase3"
    )

    // Amplitude animation for breathing effect
    val amplitude by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amplitude"
    )

    val phases = listOf(phase1, phase2, phase3)
    val amplitudes = listOf(30f, 20f, 15f)
    val alphas = listOf(alpha, alpha * 0.7f, alpha * 0.5f)
    val strokeWidths = listOf(3f, 2f, 1.5f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height * 0.5f

        for (i in 0 until minOf(waveCount, 3)) {
            val path = Path()
            val waveAmplitude = amplitudes[i] * amplitude
            val phaseOffset = Math.toRadians(phases[i].toDouble())

            path.moveTo(0f, centerY)

            for (x in 0..width.toInt() step 5) {
                val normalizedX = x / width * 4 * Math.PI
                val y = centerY + (sin(normalizedX + phaseOffset) * waveAmplitude).toFloat()
                path.lineTo(x.toFloat(), y)
            }

            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        GradientStart.copy(alpha = alphas[i]),
                        GradientMiddle.copy(alpha = alphas[i]),
                        Primary.copy(alpha = alphas[i])
                    )
                ),
                style = Stroke(
                    width = strokeWidths[i],
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Composable
fun SoundWaveBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.15f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_wave")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw multiple horizontal wave lines across the screen
        val lineCount = 8
        val spacing = height / (lineCount + 1)

        for (i in 1..lineCount) {
            val path = Path()
            val y = spacing * i
            val waveOffset = Math.toRadians((offset + i * 30).toDouble())

            path.moveTo(0f, y)

            for (x in 0..width.toInt() step 8) {
                val normalizedX = x / width * 2 * Math.PI
                val waveY = y + (sin(normalizedX + waveOffset) * 8).toFloat()
                path.lineTo(x.toFloat(), waveY)
            }

            val lineAlpha = alpha * (1f - (kotlin.math.abs(i - lineCount / 2f) / (lineCount / 2f)) * 0.5f)

            drawPath(
                path = path,
                color = Primary.copy(alpha = lineAlpha),
                style = Stroke(width = 1f, cap = StrokeCap.Round)
            )
        }
    }
}
