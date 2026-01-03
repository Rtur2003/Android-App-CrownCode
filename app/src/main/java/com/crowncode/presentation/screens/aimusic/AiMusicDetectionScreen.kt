package com.crowncode.presentation.screens.aimusic

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.presentation.theme.Accent
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.BorderLight
import com.crowncode.presentation.theme.Error
import com.crowncode.presentation.theme.GlowColor
import com.crowncode.presentation.theme.GradientEnd
import com.crowncode.presentation.theme.GradientMiddle
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.Secondary
import com.crowncode.presentation.theme.Success
import com.crowncode.presentation.theme.Surface
import com.crowncode.presentation.theme.SurfaceAlt
import com.crowncode.presentation.theme.SurfaceElevated
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextOnPrimary
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import com.crowncode.presentation.theme.Warning

@Composable
fun AiMusicDetectionScreen(
    viewModel: AiMusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var urlInput by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileName = it.lastPathSegment ?: "Seçilen dosya"
            viewModel.onFileSelected(it)
        }
    }

    Box(
        modifier = Modifier
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

        // Decorative glow effect
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-100).dp)
                .blur(100.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header
            HeaderSection()

            Spacer(modifier = Modifier.height(32.dp))

            when (uiState) {
                is AiMusicUiState.Idle -> {
                    // Upload Card
                    UploadCard(
                        onUploadClick = { filePickerLauncher.launch("audio/*") },
                        selectedFileName = selectedFileName,
                        onAnalyzeFile = { viewModel.analyzeFile() }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Divider with text
                    DividerWithText(text = "veya")

                    Spacer(modifier = Modifier.height(20.dp))

                    // URL Input Card
                    UrlInputCard(
                        urlInput = urlInput,
                        onUrlChange = { urlInput = it },
                        onAnalyzeUrl = {
                            keyboardController?.hide()
                            viewModel.analyzeUrl(urlInput)
                        },
                        isEnabled = urlInput.isNotBlank()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Info Card
                    InfoCard()
                }

                is AiMusicUiState.Processing -> {
                    ProcessingView(
                        currentStep = (uiState as AiMusicUiState.Processing).currentStep
                    )
                }

                is AiMusicUiState.Success -> {
                    ResultView(
                        result = (uiState as AiMusicUiState.Success).result,
                        onAnalyzeAnother = {
                            urlInput = ""
                            selectedFileName = null
                            viewModel.reset()
                        }
                    )
                }

                is AiMusicUiState.Error -> {
                    ErrorView(
                        message = (uiState as AiMusicUiState.Error).message,
                        onRetry = {
                            urlInput = ""
                            selectedFileName = null
                            viewModel.reset()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "header_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo/Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = TextOnPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title with gradient
        Text(
            text = "AI Müzik Tespiti",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Müziğin yapay zeka tarafından mı oluşturulduğunu tespit edin",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Accuracy badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Primary.copy(alpha = 0.12f))
                .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
    }
}

@Composable
private fun UploadCard(
    onUploadClick: () -> Unit,
    selectedFileName: String?,
    onAnalyzeFile: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Dosya Yükle",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "MP3, WAV, FLAC, M4A",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upload Zone
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceAlt)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                BorderLight.copy(alpha = 0.5f),
                                Primary.copy(alpha = 0.3f),
                                BorderLight.copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUploadClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Dosya seçmek için dokunun",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            // Selected File
            AnimatedVisibility(
                visible = selectedFileName != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceElevated)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = selectedFileName ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        text = "Analiz Et",
                        onClick = onAnalyzeFile,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun DividerWithText(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Border)
                    )
                )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Border, Color.Transparent)
                    )
                )
        )
    }
}

@Composable
private fun UrlInputCard(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onAnalyzeUrl: () -> Unit,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Secondary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = Secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "URL ile Analiz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "YouTube linki yapıştırın",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "https://youtube.com/watch?v=...",
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Border,
                    cursorColor = Primary,
                    focusedContainerColor = SurfaceAlt,
                    unfocusedContainerColor = SurfaceAlt
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (isEnabled) onAnalyzeUrl() }),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradientButton(
                text = "Analiz Et",
                onClick = onAnalyzeUrl,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) {
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                GradientStart.copy(alpha = 0.3f),
                                GradientEnd.copy(alpha = 0.3f)
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) TextOnPrimary else TextMuted
            )
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Warning.copy(alpha = 0.08f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(Warning.copy(alpha = 0.3f), Warning.copy(alpha = 0.1f))
            )
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Warning,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Demo Modu",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Warning
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Şu an için simüle edilmiş sonuçlar gösterilmektedir. Gerçek AI modeli entegrasyonu yakında.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ProcessingView(currentStep: ProcessingStep) {
    val infiniteTransition = rememberInfiniteTransition(label = "processing")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Animated processing indicator
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulse),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow
            Canvas(modifier = Modifier.size(120.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
            }

            // Rotating arc
            Canvas(modifier = Modifier.size(100.dp)) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Primary,
                            Accent,
                            Color.Transparent
                        )
                    ),
                    startAngle = rotation,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // Center icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Analiz Ediliyor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Yapay zeka modeli çalışıyor...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Processing steps
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                ProcessingStep.entries.forEach { step ->
                    ProcessingStepRow(
                        step = step,
                        isActive = step == currentStep,
                        isCompleted = step.ordinal < currentStep.ordinal
                    )
                    if (step != ProcessingStep.entries.last()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProcessingStepRow(
    step: ProcessingStep,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Success
                        isActive -> Primary
                        else -> SurfaceAlt
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TextOnPrimary,
                    modifier = Modifier.size(18.dp)
                )
                isActive -> CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = TextOnPrimary,
                    strokeWidth = 2.dp
                )
                else -> Text(
                    text = "${step.ordinal + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
            }
        }

        Text(
            text = step.displayName,
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                isActive -> TextPrimary
                isCompleted -> Success
                else -> TextMuted
            },
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun ResultView(
    result: AnalysisResult,
    onAnalyzeAnother: () -> Unit
) {
    val isAI = result.isAiGenerated
    val resultColor = if (isAI) Warning else Success

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.08f)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(resultColor.copy(alpha = 0.4f), resultColor.copy(alpha = 0.1f))
                )
            )
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Result Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(resultColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isAI) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = resultColor,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isAI) "AI Tarafından Oluşturulmuş" else "İnsan Tarafından Yapılmış",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = resultColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confidence
                Text(
                    text = "Güven Oranı: %${(result.confidence * 100).toInt()}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Metrics Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "Süre",
                        value = "${result.processingTime.toString().take(3)}s"
                    )
                    MetricItem(
                        label = "Sample Rate",
                        value = "${result.sampleRate / 1000}kHz"
                    )
                    MetricItem(
                        label = "Uzunluk",
                        value = "${result.duration}s"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // New Analysis Button
        GradientButton(
            text = "Yeni Analiz",
            onClick = onAnalyzeAnother,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun MetricItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Error.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = Error,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Bir Hata Oluştu",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceElevated
            ),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tekrar Dene",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
