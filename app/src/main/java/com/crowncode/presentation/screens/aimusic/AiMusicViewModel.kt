package com.crowncode.presentation.screens.aimusic

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.usecase.AnalyzeAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProcessingStep(val displayName: String) {
    VALIDATING("Doğrulanıyor"),
    UPLOADING("Yükleniyor"),
    ANALYZING("Analiz Ediliyor"),
    COMPLETE("Tamamlandı")
}

sealed interface AiMusicUiState {
    data object Idle : AiMusicUiState
    data class Processing(val currentStep: ProcessingStep) : AiMusicUiState
    data class Success(val result: AnalysisResult) : AiMusicUiState
    data class Error(val message: String) : AiMusicUiState
}

@HiltViewModel
class AiMusicViewModel @Inject constructor(
    private val analyzeAudioUseCase: AnalyzeAudioUseCase,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiMusicUiState>(AiMusicUiState.Idle)
    val uiState: StateFlow<AiMusicUiState> = _uiState.asStateFlow()

    private var selectedFileUri: Uri? = null

    fun onFileSelected(uri: Uri) {
        selectedFileUri = uri
    }

    fun analyzeFile() {
        val uri = selectedFileUri
        if (uri == null) {
            _uiState.value = AiMusicUiState.Error("Lütfen bir dosya seçin")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.VALIDATING)

                val contentResolver = application.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "audio/mpeg"
                val fileName = uri.lastPathSegment ?: "audio_file"

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.UPLOADING)

                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Dosya okunamadı")

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING)

                val result = inputStream.use {
                    analyzeAudioUseCase.analyzeFile(it, fileName, mimeType)
                }

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                _uiState.value = AiMusicUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = AiMusicUiState.Error(
                    mapErrorMessage(e)
                )
            }
        }
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

        viewModelScope.launch {
            try {
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.VALIDATING)
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING)

                val result = analyzeAudioUseCase.analyzeUrl(url)

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                _uiState.value = AiMusicUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = AiMusicUiState.Error(
                    mapErrorMessage(e)
                )
            }
        }
    }

    private fun mapErrorMessage(e: Exception): String {
        return when {
            e.message?.contains("rate_limit") == true ->
                "Çok fazla istek gönderildi. Lütfen bekleyin."
            e.message?.contains("file_too_large") == true ->
                "Dosya çok büyük (maks. 30MB)"
            e.message?.contains("file_too_small") == true ->
                "Dosya çok küçük"
            e.message?.contains("invalid_file_type") == true ->
                "Geçersiz dosya türü. Lütfen ses dosyası yükleyin."
            e.message?.contains("invalid_youtube_url") == true ->
                "Geçersiz YouTube URL'si"
            e.message?.contains("youtube_analysis_failed") == true ->
                "YouTube analizi başarısız oldu"
            e.message?.contains("Unable to resolve host") == true ||
            e.message?.contains("Failed to connect") == true ->
                "Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin."
            e.message?.contains("timeout") == true ->
                "İstek zaman aşımına uğradı. Tekrar deneyin."
            else ->
                e.message ?: "Bilinmeyen bir hata oluştu"
        }
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
