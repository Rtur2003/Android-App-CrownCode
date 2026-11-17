package com.crowncode.presentation.screens.aimusic

import android.Manifest
import android.content.Intent
import android.net.Uri
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.model.AnalysisTimeline
import com.crowncode.domain.model.ServerHealthState
import com.crowncode.presentation.components.GradientButton
import com.crowncode.presentation.theme.Accent
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.BorderLight
import com.crowncode.presentation.theme.Error
import com.crowncode.presentation.theme.GradientEnd
import com.crowncode.presentation.theme.GradientMiddle
import com.crowncode.presentation.theme.GradientStart
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.Success
import com.crowncode.presentation.theme.Surface
import com.crowncode.presentation.theme.SurfaceAlt
import com.crowncode.presentation.theme.SurfaceElevated
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextOnPrimary
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import com.crowncode.presentation.theme.Warning
import kotlin.math.cos
import kotlin.math.sin

private enum class InputMode(val label: String, val icon: ImageVector) {
    MIC("Dinle", Icons.Default.Mic),
    FILE("Dosya", Icons.Default.CloudUpload),
    URL("Link", Icons.Default.Link)
}

@Composable
fun AiMusicDetectionScreen(
    onNavigateToHistory: () -> Unit = {},
    viewModel: AiMusicViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val serverHealth by viewModel.serverHealth.collectAsStateWithLifecycle()
    var inputMode by remember { mutableStateOf(InputMode.MIC) }
    var urlInput by remember { mutableStateOf("") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val clipboard = LocalClipboardManager.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedFileName = viewModel.resolveFileName(it)
            viewModel.onFileSelected(it)
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startRecording()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Obsidian atmospheric background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Surface.copy(alpha = 0.88f),
                            Background,
                            Background
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-60).dp, y = (-110).dp)
                .blur(110.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Primary.copy(alpha = 0.16f),
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
                .padding(bottom = 36.dp)
        ) {
            // Top bar: Live Backend Status Pill + History shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServerStatusPill(
                    state = serverHealth,
                    onClick = { viewModel.refreshServerStatus() }
                )

                IconButton(onClick = onNavigateToHistory) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Geçmiş",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState) {
                is AiMusicUiState.Idle -> {
                    // Clean Mode Capsule Switcher (1 single compact studio console)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(SurfaceAlt)
                            .border(1.dp, Border, RoundedCornerShape(999.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        InputMode.entries.forEach { mode ->
                            val selected = mode == inputMode
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(
                                        if (selected) Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
                                        else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                                    )
                                    .clickable { inputMode = mode }
                                    .padding(vertical = 11.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = null,
                                    tint = if (selected) TextOnPrimary else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = mode.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (selected) TextOnPrimary else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    AnimatedContent(
                        targetState = inputMode,
                        transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                        label = "input_mode_transition"
                    ) { mode ->
                        when (mode) {
                            InputMode.MIC -> OrbitalMicStage(
                                onStartRecording = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            )
                            InputMode.FILE -> UploadCard(
                                onUploadClick = { filePickerLauncher.launch("audio/*") },
                                selectedFileName = selectedFileName,
                                onAnalyzeFile = { viewModel.analyzeFile() }
                            )
                            InputMode.URL -> UrlInputCard(
                                urlInput = urlInput,
                                onUrlChange = { urlInput = it },
                                onPasteClipboard = {
                                    clipboard.getText()?.text?.trim()?.takeIf { it.isNotEmpty() }?.let { pasted ->
                                        urlInput = pasted
                                    }
                                },
                                onAnalyzeUrl = {
                                    keyboardController?.hide()
                                    viewModel.analyzeUrl(urlInput)
                                },
                                isEnabled = urlInput.isNotBlank()
                            )
                        }
                    }
                }

                is AiMusicUiState.Recording -> {
                    val recordingState = uiState as AiMusicUiState.Recording
                    RecordingView(
                        elapsedSeconds = recordingState.elapsedSeconds,
                        amplitude = recordingState.amplitude,
                        onStop = { viewModel.stopRecording() }
                    )
                }

                is AiMusicUiState.Processing -> {
                    val proc = uiState as AiMusicUiState.Processing
                    ProcessingView(
                        currentStep = proc.currentStep,
                        statusDetail = proc.statusDetail
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
private fun ServerStatusPill(
    state: ServerHealthState,
    onClick: () -> Unit
) {
    val dotColor = when (state) {
        is ServerHealthState.Online -> Success
        is ServerHealthState.Waking, ServerHealthState.Checking -> Warning
        is ServerHealthState.Offline -> Error
    }
    val label = when (state) {
        is ServerHealthState.Checking -> "AURIS bağlanıyor..."
        is ServerHealthState.Online ->
            if (state.ensembleCount > 0) "AURIS Çevrimiçi · ${state.ensembleCount} XAI" else "AURIS Çevrimiçi"
        is ServerHealthState.Waking -> "Motor uyanıyor · Dokun yenile"
        is ServerHealthState.Offline -> "Çevrimdışı · Dokun tekrar dene"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SurfaceAlt)
            .border(1.dp, BorderLight, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HeaderSection() {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "header_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AURIS Ses Laboratuvarı",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Dinle, dosya seç veya link yapıştır. LightGBM + Wav2Vec2 anında doğrulasın.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun OrbitalMicStage(
    onStartRecording: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_halo")
    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(176.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(164.dp)
                        .scale(haloScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Primary.copy(alpha = 0.22f), Color.Transparent)
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(CircleShape)
                        .border(1.dp, Primary.copy(alpha = 0.35f), CircleShape)
                        .background(SurfaceAlt),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(GradientStart, GradientMiddle, GradientEnd))
                            )
                            .clickable(onClick = onStartRecording),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Dinlemeyi Başlat",
                            tint = TextOnPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Dinlemek İçin Dokun",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Çalan müziği 30 saniyeye kadar yakala ve anında analiz et",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = TextAlign.Center
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
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(156.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceAlt)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(BorderLight, Primary.copy(alpha = 0.45f), BorderLight)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(onClick = onUploadClick),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ses dosyası seç (MP3, WAV, FLAC, M4A)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "En fazla 30 MB · Orijinal dosya adı korunur",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

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
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceElevated)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = selectedFileName ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    GradientButton(
                        text = "Dosyayı Analiz Et",
                        onClick = onAnalyzeFile,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun UrlInputCard(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onPasteClipboard: () -> Unit,
    onAnalyzeUrl: () -> Unit,
    isEnabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YouTube · TikTok · Instagram · SoundCloud",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                TextButton(onClick = onPasteClipboard) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Yapıştır", color = Primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = urlInput,
                onValueChange = onUrlChange,
                placeholder = {
                    Text(
                        "https://www.youtube.com/watch?v=...",
                        color = TextMuted
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { if (isEnabled) onAnalyzeUrl() }),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = BorderLight,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = Primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            GradientButton(
                text = "Bağlantıyı Analiz Et",
                onClick = onAnalyzeUrl,
                enabled = isEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ProcessingView(
    currentStep: ProcessingStep,
    statusDetail: String? = null
) {
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
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(pulse),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Primary.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
            }

            Canvas(modifier = Modifier.size(100.dp)) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color.Transparent, Primary, Accent, Color.Transparent)
                    ),
                    startAngle = rotation,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analiz Ediliyor",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = statusDetail ?: "Yapay zeka modeli çalışıyor...",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                ProcessingStep.entries.forEach { step ->
                    ProcessingStepRow(
                        step = step,
                        isActive = step == currentStep,
                        isCompleted = step.ordinal < currentStep.ordinal
                    )
                    if (step != ProcessingStep.entries.last()) {
                        Spacer(modifier = Modifier.height(14.dp))
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
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
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
                    modifier = Modifier.size(16.dp)
                )
                isActive -> CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
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
    val context = LocalContext.current
    val isAI = result.isAiGenerated
    val resultColor = if (isAI) Warning else Success
    val threshold = result.xai?.threshold ?: 0.4316f

    Column(modifier = Modifier.fillMaxWidth()) {
        // Verdict + Animated Radial Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = resultColor.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, resultColor.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AurisVerdictGauge(
                    probability = result.confidence,
                    threshold = threshold,
                    accentColor = resultColor
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAI) "AI Tarafından Oluşturulmuş" else "İnsan Tarafından Yapılmış",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = resultColor,
                    textAlign = TextAlign.Center
                )

                result.xai?.confidenceBand?.labelTr?.takeIf { it.isNotBlank() }?.let { bandLabel ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(resultColor.copy(alpha = 0.14f))
                            .border(1.dp, resultColor.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Kesinlik Bandı: $bandLabel",
                            style = MaterialTheme.typography.labelMedium,
                            color = resultColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${result.modelVersion} · ${result.decisionSource}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem("İşlem", "${String.format(Locale.US, "%.1f", result.processingTime)}s")
                    MetricItem("Örnekleme", "${result.audioInfo.sampleRate / 1000}kHz")
                    MetricItem("Süre", "${result.audioInfo.duration.toInt()}s")
                }
            }
        }

        // Whole-Track Waveform Heat Timeline if available
        result.timeline?.let { timeline ->
            if (timeline.peaks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                TimelineWaveformCard(timeline = timeline)
            }
        }

        // Acoustic Details Card
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
            border = BorderStroke(1.dp, Border)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Analiz Detayları",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))
                FeatureBar("Spektral Düzenlilik", result.features.spectralRegularity)
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBar("Zamansal Örüntüler", result.features.temporalPatterns)
                Spacer(modifier = Modifier.height(8.dp))
                FeatureBar("Harmonik Yapı", result.features.harmonicStructure)

                if (result.features.indicators.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Göstergeler: ${result.features.indicators.joinToString(" · ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                result.vocalAnalysis?.let { vocal ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Vokal Analizi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FeatureBar("Vokal AI Skoru", vocal.vocalAiScore)
                    Spacer(modifier = Modifier.height(4.dp))
                    FeatureBar("Pitch Kararlılığı", vocal.pitchStabilityScore)
                }
            }
        }

        // XAI & Meta-classifier Card
        if (result.xai != null || result.metaClassifier != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Surface),
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "XAI Açıklanabilirlik ve Model Oyları",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    result.xai?.let { xai ->
                        val activeVotes = xai.modelVotes.filter { it.available }
                        if (activeVotes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            activeVotes.forEach { vote ->
                                val tone = if (vote.vote == "ai") Warning else Success
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(vote.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text(
                                        text = "${if (vote.vote == "ai") "AI" else "İnsan"} · %${(vote.probability * 100).toInt()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = tone
                                    )
                                }
                            }
                        }

                        if (xai.topContributions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "En Etkili Akustik Öznitelikler (${xai.featureCount} özellik)",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            xai.topContributions.take(4).forEach { contrib ->
                                val tone = if (contrib.direction == "towards_ai") Warning else Success
                                val signed = if (contrib.shapValue >= 0f) {
                                    "+${String.format(Locale.US, "%.2f", contrib.shapValue)}"
                                } else {
                                    String.format(Locale.US, "%.2f", contrib.shapValue)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(contrib.label.ifBlank { contrib.name }, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                    Text(signed, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = tone)
                                }
                            }
                        }
                    }

                    result.metaClassifier?.let { meta ->
                        Spacer(modifier = Modifier.height(12.dp))
                        val metaTone = if (meta.isAiGenerated) Warning else Success
                        Text(
                            text = "İkinci Görüş (Meta): ${if (meta.isAiGenerated) "AI" else "İnsan"} · %${(meta.confidence * 100).toInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = metaTone
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        GradientButton(
            text = "Yeni Analiz",
            onClick = onAnalyzeAnother,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                val verdictText = if (isAI) "AI Tarafından Oluşturulmuş" else "İnsan Tarafından Yapılmış"
                val shareBody = buildString {
                    append("AURIS Analiz Sonucu: $verdictText\n")
                    append("AI Olasılığı: %${(result.confidence * 100).toInt()}\n")
                    append("Model: ${result.modelVersion} (${result.decisionSource})\n")
                    append("https://hasan-arthur-altuntas.xyz/ai-music-detection")
                }
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareBody)
                }
                context.startActivity(Intent.createChooser(sendIntent, "Sonucu Paylaş"))
            },
            border = BorderStroke(1.dp, Primary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sonucu Paylaş", color = Primary)
        }
    }
}

@Composable
private fun AurisVerdictGauge(
    probability: Float,
    threshold: Float,
    accentColor: Color
) {
    val target = probability.coerceIn(0f, 1f)
    val animatedProb by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "gauge_sweep"
    )

    Box(
        modifier = Modifier.size(152.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(144.dp)) {
            val strokePx = 12.dp.toPx()
            val inset = strokePx / 2f
            val arcSize = Size(size.width - strokePx, size.height - strokePx)
            val startAngle = 150f
            val fullSweep = 240f

            // Track
            drawArc(
                color = SurfaceAlt,
                startAngle = startAngle,
                sweepAngle = fullSweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Success, accentColor, accentColor)
                ),
                startAngle = startAngle,
                sweepAngle = fullSweep * animatedProb,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Threshold notch
            val thresholdRad = Math.toRadians((startAngle + fullSweep * threshold.coerceIn(0.05f, 0.95f)).toDouble())
            val radius = arcSize.width / 2f
            val cx = size.width / 2f
            val cy = size.height / 2f
            val rInner = radius - strokePx * 0.7f
            val rOuter = radius + strokePx * 0.7f
            drawLine(
                color = Primary,
                startAngleOffset(cx, cy, rInner, thresholdRad),
                startAngleOffset(cx, cy, rOuter, thresholdRad),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "%${(probability * 100).toInt()}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
            Text(
                text = "AI Olasılığı",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

private fun startAngleOffset(cx: Float, cy: Float, r: Float, rad: Double): Offset =
    Offset(cx + (r * cos(rad)).toFloat(), cy + (r * sin(rad)).toFloat())

@Composable
private fun TimelineWaveformCard(timeline: AnalysisTimeline) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Parça Zaman Çizelgesi",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "${timeline.flaggedCount}/${timeline.scoredCount} AI pencere",
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceAlt)
                    .padding(horizontal = 8.dp, vertical = 10.dp)
            ) {
                val peaks = timeline.peaks
                if (peaks.isNotEmpty()) {
                    val count = peaks.size
                    val barGap = 2.dp.toPx()
                    val barW = ((size.width - barGap * (count - 1)) / count).coerceAtLeast(2f)
                    val segCount = timeline.segments.size
                    peaks.forEachIndexed { idx, amp ->
                        val seg = if (segCount > 0) {
                            timeline.segments[((idx.toFloat() / count) * segCount).toInt().coerceIn(0, segCount - 1)]
                        } else null
                        val barColor = when {
                            seg?.isAi == true -> Warning
                            seg?.state == "ok" -> Success
                            else -> TextMuted.copy(alpha = 0.45f)
                        }
                        val h = (amp.coerceIn(0.08f, 1f) * size.height)
                        val x = idx * (barW + barGap)
                        val y = (size.height - h) / 2f
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barW, h),
                            cornerRadius = CornerRadius(barW / 2f, barW / 2f)
                        )
                    }
                }
                // Baseline subtle center rule
                drawLine(
                    color = BorderLight,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ortalama AI yoğunluğu %${(timeline.meanProbability * 100).toInt()} · Eşik %${(timeline.threshold * 100).toInt()}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun FeatureBar(label: String, value: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(SurfaceAlt)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = value.coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(GradientStart, GradientEnd)))
            )
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun RecordingView(
    elapsedSeconds: Int,
    amplitude: Float,
    onStop: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recording")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(animation = tween(600), repeatMode = RepeatMode.Reverse),
        label = "pulse"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "ring"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dinleniyor...", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Çalan müziği mikrofona tutun", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        Spacer(modifier = Modifier.height(36.dp))

        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size((120 + amplitude * 40).dp)
                    .scale(pulse)
                    .clip(CircleShape)
                    .background(Error.copy(alpha = ringAlpha * amplitude.coerceAtLeast(0.3f)))
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Error.copy(alpha = 0.2f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Error)
                    .clickable(onClick = onStop),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Durdur ve Analiz Et",
                    tint = TextOnPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        Text(
            text = String.format(Locale.US, "%d:%02d / 0:30", minutes, seconds),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("Analize göndermek için kırmızı butona dokunun", style = MaterialTheme.typography.bodySmall, color = TextMuted)
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
            .padding(vertical = 40.dp),
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

        Spacer(modifier = Modifier.height(20.dp))
        Text("Bir Hata Oluştu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
            modifier = Modifier.height(54.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tekrar Dene", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }
    }
}
