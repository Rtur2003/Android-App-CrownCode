package com.crowncode.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.crowncode.R

enum class LogoSize(val size: Dp) {
    Large(120.dp),
    Medium(72.dp),
    Small(56.dp)
}

@Composable
fun CrownCodeLogo(
    modifier: Modifier = Modifier,
    size: LogoSize = LogoSize.Large,
    animated: Boolean = true
) {
    var isVisible by remember { mutableStateOf(!animated) }

    LaunchedEffect(Unit) {
        if (animated) {
            isVisible = true
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "logo_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "logo_alpha"
    )

    Image(
        painter = painterResource(id = R.drawable.logo_transparent),
        contentDescription = "CrownCode Logo",
        modifier = modifier
            .size(size.size)
            .scale(if (animated) scale else 1f)
            .alpha(if (animated) alpha else 1f)
    )
}
