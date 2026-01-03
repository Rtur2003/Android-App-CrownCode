package com.crowncode.presentation.screens.aimusic

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

enum class ProcessingStep(val displayName: String) {
    VALIDATING("Doğrulanıyor"),
    DOWNLOADING("İndiriliyor"),
    ANALYZING("Analiz Ediliyor"),
    COMPLETE("Tamamlandı")
}

data class AnalysisResult(
    val isAiGenerated: Boolean,
    val confidence: Float,
    val processingTime: Float,
    val sampleRate: Int,
    val duration: Int,
    val modelVersion: String = "wav2vec2-v1.0"
)

sealed interface AiMusicUiState {
    data object Idle : AiMusicUiState
    data class Processing(val currentStep: ProcessingStep) : AiMusicUiState
    data class Success(val result: AnalysisResult) : AiMusicUiState
    data class Error(val message: String) : AiMusicUiState
}

@HiltViewModel
class AiMusicViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<AiMusicUiState>(AiMusicUiState.Idle)
    val uiState: StateFlow<AiMusicUiState> = _uiState.asStateFlow()

    private var selectedFileUri: Uri? = null

    fun onFileSelected(uri: Uri) {
        selectedFileUri = uri
    }

    fun analyzeFile() {
        if (selectedFileUri == null) {
            _uiState.value = AiMusicUiState.Error("Lütfen bir dosya seçin")
            return
        }
        runAnalysis()
    }

    fun analyzeUrl(url: String) {
        if (url.isBlank()) {
            _uiState.value = AiMusicUiState.Error("Lütfen bir URL girin")
            return
        }

        if (!isValidYoutubeUrl(url)) {
            _uiState.value = AiMusicUiState.Error("Geçersiz YouTube URL'si")
            return
        }

        runAnalysis()
    }

    private fun runAnalysis() {
        viewModelScope.launch {
            try {
                // Validating
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.VALIDATING)
                delay(800)

                // Downloading
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.DOWNLOADING)
                delay(1200)

                // Analyzing
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING)
                delay(1500)

                // Complete
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                delay(500)

                // Generate mock result (Demo mode)
                val result = generateMockResult()
                _uiState.value = AiMusicUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = AiMusicUiState.Error(e.message ?: "Bilinmeyen hata oluştu")
            }
        }
    }

    private fun generateMockResult(): AnalysisResult {
        // Demo mode - simulated results
        val isAi = Random.nextBoolean()
        return AnalysisResult(
            isAiGenerated = isAi,
            confidence = if (isAi) Random.nextFloat() * 0.3f + 0.7f else Random.nextFloat() * 0.3f + 0.7f,
            processingTime = Random.nextFloat() * 1.5f + 0.8f,
            sampleRate = listOf(44100, 48000, 96000).random(),
            duration = Random.nextInt(60, 300)
        )
    }

    private fun isValidYoutubeUrl(url: String): Boolean {
        val youtubePatterns = listOf(
            "youtube.com/watch",
            "youtu.be/",
            "youtube.com/v/",
            "youtube.com/embed/"
        )
        return youtubePatterns.any { url.contains(it, ignoreCase = true) }
    }

    fun reset() {
        _uiState.value = AiMusicUiState.Idle
        selectedFileUri = null
    }
}
