package com.crowncode.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crowncode.presentation.theme.GradientEnd
import com.crowncode.presentation.theme.GradientMiddle
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.Primary
import kotlin.random.Random

@Composable
fun EqualizerBars(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    maxHeight: Dp = 40.dp,
    barWidth: Dp = 4.dp,
    spacing: Dp = 3.dp,
    isAnimating: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    // Generate random delays for each bar
    val delays = remember { List(barCount) { Random.nextInt(0, 300) } }
    val durations = remember { List(barCount) { Random.nextInt(300, 600) } }
    val minHeights = remember { List(barCount) { Random.nextFloat() * 0.2f + 0.15f } }
    val maxHeights = remember { List(barCount) { Random.nextFloat() * 0.4f + 0.6f } }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until barCount) {
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = minHeights[i],
                targetValue = if (isAnimating) maxHeights[i] else minHeights[i],
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = durations[i],
                        delayMillis = delays[i],
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxHeight * heightFraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                GradientStart,
                                GradientMiddle,
                                GradientEnd
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun EqualizerBarsLarge(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = true
) {
    EqualizerBars(
        modifier = modifier,
        barCount = 7,
        maxHeight = 60.dp,
        barWidth = 6.dp,
        spacing = 4.dp,
        isAnimating = isAnimating
    )
}

@Composable
fun EqualizerBarsSmall(
    modifier: Modifier = Modifier,
    isAnimating: Boolean = true
) {
    EqualizerBars(
        modifier = modifier,
        barCount = 5,
        maxHeight = 24.dp,
        barWidth = 3.dp,
        spacing = 2.dp,
        isAnimating = isAnimating
    )
}
