package com.crowncode.presentation.screens.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crowncode.R
import com.crowncode.presentation.components.AurisLogo
import com.crowncode.presentation.components.AurisLogoSize
import com.crowncode.presentation.components.EqualizerBars
import com.crowncode.presentation.components.GradientButton
import com.crowncode.presentation.components.OutlinedActionButton
import com.crowncode.presentation.components.SoundWaveBackground
import com.crowncode.presentation.theme.AurisBackground
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
    var isContentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isContentVisible = true
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (isContentVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "content_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AurisBackground)
    ) {
        // Animated sound wave background
        SoundWaveBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.08f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Auris AI Logo with glow
            AurisLogo(
                size = AurisLogoSize.Large,
                animated = true,
                showSubtitle = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Equalizer animation
            EqualizerBars(
                barCount = 7,
                maxHeight = 48.dp,
                barWidth = 5.dp,
                spacing = 4.dp,
                isAnimating = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main headline
            Text(
                text = "Müziğin Gerçeğini\nKeşfedin",
                fontFamily = PortmanteauFamily,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = "Yapay zeka destekli analiz ile müziğinizin\nAI tarafından mı yoksa insan tarafından mı\noluşturulduğunu anında tespit edin",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Accuracy Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Primary.copy(alpha = 0.1f))
                    .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Success)
                )
                Text(
                    text = "%97.2 Doğruluk Oranı",
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // CrownCode branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_transparent),
                    contentDescription = "CrownCode Logo",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "by CrownCode",
                    fontFamily = ImFellDoublePicaFamily,
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Column(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
