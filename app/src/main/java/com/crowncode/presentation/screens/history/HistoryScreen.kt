package com.crowncode.presentation.screens.history

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.crowncode.data.local.entity.AnalysisHistoryEntity
import com.crowncode.presentation.theme.Background
import com.crowncode.presentation.theme.Border
import com.crowncode.presentation.theme.Primary
import com.crowncode.presentation.theme.Success
import com.crowncode.presentation.theme.Surface
import com.crowncode.presentation.theme.TextMuted
import com.crowncode.presentation.theme.TextPrimary
import com.crowncode.presentation.theme.TextSecondary
import com.crowncode.presentation.theme.Warning
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Geri",
                    tint = TextPrimary
                )
            }
            Text(
                text = "Analiz Geçmişi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (history.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearAll() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Temizle", color = TextMuted)
                }
            }
        }

        if (history.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Henüz analiz yapılmadı",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Yaptığınız analizler burada görünecek",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history, key = { it.id }) { entry ->
                    HistoryCard(
                        entry = entry,
                        onDelete = { viewModel.deleteEntry(entry.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    entry: AnalysisHistoryEntity,
    onDelete: () -> Unit
) {
    val resultColor = if (entry.isAiGenerated) Warning else Success
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale("tr"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source type icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(resultColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (entry.sourceType) {
                        "microphone" -> Icons.Default.Mic
                        "file" -> Icons.Default.AudioFile
                        else -> Icons.Default.Link
                    },
                    contentDescription = null,
                    tint = resultColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (entry.isAiGenerated) Icons.Default.Warning else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = resultColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (entry.isAiGenerated) "AI" else "İnsan",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = resultColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "%${(entry.confidence * 100).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = entry.sourceName,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${dateFormat.format(Date(entry.timestamp))} · ${entry.modelVersion}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
