package com.crowncode.presentation.screens.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crowncode.presentation.components.BackgroundWithGlow
import com.crowncode.presentation.components.CrownCodeLogo
import com.crowncode.presentation.components.GradientButton
import com.crowncode.presentation.components.LogoSize
import com.crowncode.presentation.components.OutlinedActionButton
import com.crowncode.presentation.theme.GradientEnd
import com.crowncode.presentation.theme.GradientMiddle
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.ImFellDoublePicaFamily
import com.crowncode.presentation.theme.PortmanteauFamily
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.Success
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showContent = true
        delay(400)
        showButtons = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "content_alpha"
    )

    val buttonsAlpha by animateFloatAsState(
        targetValue = if (showButtons) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "buttons_alpha"
    )

    BackgroundWithGlow {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            CrownCodeLogo(
                size = LogoSize.Large,
                animated = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Brand Name
            Column(
                modifier = Modifier.alpha(contentAlpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CrownCode",
                    fontFamily = PortmanteauFamily,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagline with decorative font
                Text(
                    text = "AI Music Detection",
                    fontFamily = ImFellDoublePicaFamily,
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Decorative divider
                DecorativeDivider()

                Spacer(modifier = Modifier.height(24.dp))

                // Description
                Text(
                    text = "Müziğinizin yapay zeka tarafından mı\nyoksa insan tarafından mı oluşturulduğunu\nkeşfedin",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Accuracy Badge
                AccuracyBadge()
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonsAlpha),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GradientButton(
                    text = "Başla",
                    onClick = onGetStarted,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedActionButton(
                    text = "Giriş Yap",
                    onClick = onSignIn,
                    modifier = Modifier.fillMaxWidth()
                )

                // Guest option
                TextButton(
                    onClick = onContinueAsGuest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Misafir olarak devam et",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DecorativeDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
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

        // Diamond ornament
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientStart, GradientMiddle)
                    )
                )
        )

        Box(
            modifier = Modifier
                .weight(1f)
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

@Composable
private fun AccuracyBadge() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Primary.copy(alpha = 0.1f))
            .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Success)
        )
        Text(
            text = "%97.2 Doğruluk Oranı",
            style = MaterialTheme.typography.labelLarge,
            color = Primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
