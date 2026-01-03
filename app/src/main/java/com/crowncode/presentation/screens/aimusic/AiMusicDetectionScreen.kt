package com.crowncode.presentation.screens.aimusic

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.presentation.theme.Gold
import com.crowncode.presentation.theme.GoldDark

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Header
        HeaderSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Warning Card
        WarningCard()

        Spacer(modifier = Modifier.height(32.dp))

        when (uiState) {
            is AiMusicUiState.Idle -> {
                // Upload Section
                UploadSection(
                    onUploadClick = { filePickerLauncher.launch("audio/*") },
                    selectedFileName = selectedFileName,
                    onAnalyzeFile = { viewModel.analyzeFile() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // URL Section
                UrlSection(
                    urlInput = urlInput,
                    onUrlChange = { urlInput = it },
                    onAnalyzeUrl = {
                        keyboardController?.hide()
                        viewModel.analyzeUrl(urlInput)
                    },
                    isEnabled = urlInput.isNotBlank()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Pipeline Info
                PipelineSection()
            }

            is AiMusicUiState.Processing -> {
                ProcessingSection(
                    currentStep = (uiState as AiMusicUiState.Processing).currentStep
                )
            }

            is AiMusicUiState.Success -> {
                ResultSection(
                    result = (uiState as AiMusicUiState.Success).result,
                    onAnalyzeAnother = {
                        urlInput = ""
                        selectedFileName = null
                        viewModel.reset()
                    }
                )
            }

            is AiMusicUiState.Error -> {
                ErrorSection(
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

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Gold.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "AI Müzik Tespiti",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Yapay Zeka Müzik Tespiti",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Müziğin AI tarafından mı yoksa insan tarafından mı oluşturulduğunu tespit edin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WarningCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF9800).copy(alpha = 0.1f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFFF9800),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Demo Modu",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Mevcut Durum: Platform geliştirme aşamasındadır.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Analiz Sonuçları: Şu an için simüle edilmiş sonuçlar gösterilmektedir.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun UploadSection(
    onUploadClick: () -> Unit,
    selectedFileName: String?,
    onAnalyzeFile: () -> Unit
) {
    Column {
        Text(
            text = "Dosya Yükle",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Dropzone
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Gold.copy(alpha = 0.5f), GoldDark.copy(alpha = 0.5f))),
                    shape = RoundedCornerShape(16.dp)
                )
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onUploadClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Dosya yüklemek için dokunun",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "MP3, WAV, FLAC, M4A desteklenir",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Selected File
        AnimatedVisibility(
            visible = selectedFileName != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AudioFile,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = selectedFileName ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onAnalyzeFile,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Analiz Et")
                    }
                }
            }
        }
    }
}

@Composable
private fun UrlSection(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onAnalyzeUrl: () -> Unit,
    isEnabled: Boolean
) {
    Column {
        Text(
            text = "veya URL ile Analiz",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Source Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SourceChip(text = "YouTube", isActive = true)
            SourceChip(text = "Spotify", isActive = false, isSoon = true)
            SourceChip(text = "Apple Music", isActive = false, isSoon = true)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = urlInput,
            onValueChange = onUrlChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("YouTube video URL'si yapıştırın...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                cursorColor = Gold
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (isEnabled) onAnalyzeUrl() }),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAnalyzeUrl,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Color.Black,
                disabledContainerColor = Gold.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "URL'yi Analiz Et",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SourceChip(
    text: String,
    isActive: Boolean,
    isSoon: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) Gold.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) Gold else MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (isSoon) {
                Text(
                    text = "Yakında",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PipelineSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Analiz Hattı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            PipelineItem("Ses İndirme", "yt-dlp")
            PipelineItem("Music AI Analizi", "opsiyonel")
            PipelineItem("Ses Analizi", "opsiyonel")
            PipelineItem("Önizleme", "her zaman")

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Backend bağlantısı olmadan önizleme modu kullanılır.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PipelineItem(
    title: String,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ProcessingSection(
    currentStep: ProcessingStep
) {
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

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
                .background(Gold.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .rotate(rotation),
                color = Gold,
                strokeWidth = 4.dp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Analiz Ediliyor...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Müzik yapay zeka modelleri ile analiz ediliyor",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Processing Steps
        ProcessingStep.entries.forEach { step ->
            ProcessingStepItem(
                step = step,
                isActive = step == currentStep,
                isCompleted = step.ordinal < currentStep.ordinal
            )
        }
    }
}

@Composable
private fun ProcessingStepItem(
    step: ProcessingStep,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Color(0xFF4CAF50)
                        isActive -> Gold
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Text(
            text = step.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isActive -> MaterialTheme.colorScheme.onBackground
                isCompleted -> Color(0xFF4CAF50)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun ResultSection(
    result: AnalysisResult,
    onAnalyzeAnother: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Result Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (result.isAiGenerated)
                    Color(0xFFFF9800).copy(alpha = 0.1f)
                else
                    Color(0xFF4CAF50).copy(alpha = 0.1f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                if (result.isAiGenerated) Color(0xFFFF9800)
                                else Color(0xFF4CAF50)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (result.isAiGenerated)
                                Icons.Default.Warning
                            else
                                Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (result.isAiGenerated)
                                "AI Tarafından Oluşturulmuş"
                            else
                                "İnsan Tarafından Yapılmış",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (result.isAiGenerated)
                                Color(0xFFFF9800)
                            else
                                Color(0xFF4CAF50)
                        )
                        Text(
                            text = "Güven: ${(result.confidence * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem("İşlem Süresi", "${result.processingTime}s")
                    MetricItem("Sample Rate", "${result.sampleRate} Hz")
                    MetricItem("Süre", "${result.duration}s")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { /* Export report */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Rapor İndir")
            }

            Button(
                onClick = onAnalyzeAnother,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Yeni Analiz")
            }
        }
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
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorSection(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color(0xFFF44336),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Bir Hata Oluştu",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Tekrar Dene")
        }
    }
}
