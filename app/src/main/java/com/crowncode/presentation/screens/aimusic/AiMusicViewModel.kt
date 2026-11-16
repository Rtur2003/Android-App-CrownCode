package com.crowncode.presentation.screens.aimusic

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.usecase.AnalyzeAudioUseCase
import com.crowncode.domain.usecase.AnalysisHistoryUseCase
import com.crowncode.util.AudioFileResolver
import com.crowncode.util.AudioRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.FileInputStream
import javax.inject.Inject

enum class ProcessingStep(val displayName: String) {
    VALIDATING("Doğrulanıyor"),
    UPLOADING("Yükleniyor"),
    ANALYZING("Analiz Ediliyor"),
    COMPLETE("Tamamlandı")
}

sealed interface AiMusicUiState {
    data object Idle : AiMusicUiState
    data class Processing(
        val currentStep: ProcessingStep,
        val statusDetail: String? = null
    ) : AiMusicUiState
    data class Recording(val elapsedSeconds: Int, val amplitude: Float) : AiMusicUiState
    data class Success(val result: AnalysisResult) : AiMusicUiState
    data class Error(val message: String) : AiMusicUiState
}

@HiltViewModel
class AiMusicViewModel @Inject constructor(
    private val analyzeAudioUseCase: AnalyzeAudioUseCase,
    private val historyUseCase: AnalysisHistoryUseCase,
    private val audioRecorder: AudioRecorder,
    private val application: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<AiMusicUiState>(AiMusicUiState.Idle)
    val uiState: StateFlow<AiMusicUiState> = _uiState.asStateFlow()

    private var selectedFileUri: Uri? = null
    private var recordingJob: Job? = null
    private var analysisJob: Job? = null

    val recorderAmplitude: StateFlow<Float> = audioRecorder.amplitude
    val recorderElapsed: StateFlow<Int> = audioRecorder.elapsedSeconds

    fun onFileSelected(uri: Uri) {
        selectedFileUri = uri
    }

    fun resolveFileName(uri: Uri): String {
        return AudioFileResolver.resolveDisplayName(application.contentResolver, uri)
    }

    // ── File Analysis ──────────────────────────

    fun analyzeFile() {
        val uri = selectedFileUri
        if (uri == null) {
            _uiState.value = AiMusicUiState.Error("Lütfen bir dosya seçin")
            return
        }

        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            try {
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.VALIDATING)

                val contentResolver = application.contentResolver
                val mimeType = contentResolver.getType(uri) ?: "audio/mpeg"
                val fileName = AudioFileResolver.resolveDisplayName(contentResolver, uri)

                _uiState.value = AiMusicUiState.Processing(
                    ProcessingStep.UPLOADING,
                    "$fileName yükleniyor..."
                )

                val inputStream = contentResolver.openInputStream(uri)
                    ?: throw Exception("Dosya okunamadı")

                _uiState.value = AiMusicUiState.Processing(
                    ProcessingStep.ANALYZING,
                    "AURIS iş kuyruğuna alınıyor..."
                )

                val result = inputStream.use {
                    analyzeAudioUseCase.analyzeFile(it, fileName, mimeType) { status ->
                        _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING, status)
                    }
                }

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                _uiState.value = AiMusicUiState.Success(result)
                saveToHistory(result, "file", fileName)

            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _uiState.value = AiMusicUiState.Error(mapErrorMessage(e))
            }
        }
    }

    // ── URL Analysis ───────────────────────────

    fun analyzeUrl(url: String) {
        if (url.isBlank()) {
            _uiState.value = AiMusicUiState.Error("Lütfen bir URL girin")
            return
        }

        if (!isValidMediaUrl(url)) {
            _uiState.value = AiMusicUiState.Error("Geçersiz URL. YouTube, TikTok, Instagram veya SoundCloud linki girin.")
            return
        }

        analysisJob?.cancel()
        analysisJob = viewModelScope.launch {
            try {
                _uiState.value = AiMusicUiState.Processing(ProcessingStep.VALIDATING)
                _uiState.value = AiMusicUiState.Processing(
                    ProcessingStep.ANALYZING,
                    "Bağlantı AURIS iş kuyruğuna alınıyor..."
                )

                val result = analyzeAudioUseCase.analyzeUrl(url) { status ->
                    _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING, status)
                }

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                _uiState.value = AiMusicUiState.Success(result)
                saveToHistory(result, detectSourceLabel(url), url)

            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                _uiState.value = AiMusicUiState.Error(mapErrorMessage(e))
            }
        }
    }

    // ── Microphone Recording (Shazam-like) ─────

    fun startRecording() {
        if (recordingJob?.isActive == true) return

        recordingJob = viewModelScope.launch {
            _uiState.value = AiMusicUiState.Recording(0, 0f)

            val amplitudeJob = launch {
                audioRecorder.amplitude.collect { amp ->
                    val state = _uiState.value
                    if (state is AiMusicUiState.Recording) {
                        _uiState.value = state.copy(amplitude = amp)
                    }
                }
            }
            val elapsedJob = launch {
                audioRecorder.elapsedSeconds.collect { sec ->
                    val state = _uiState.value
                    if (state is AiMusicUiState.Recording) {
                        _uiState.value = state.copy(elapsedSeconds = sec)
                    }
                }
            }

            try {
                val wavFile = audioRecorder.record(application)

                amplitudeJob.cancel()
                elapsedJob.cancel()

                _uiState.value = AiMusicUiState.Processing(
                    ProcessingStep.UPLOADING,
                    "Mikrofon kaydı yükleniyor..."
                )

                val result = try {
                    FileInputStream(wavFile).use { fis ->
                        analyzeAudioUseCase.analyzeFile(fis, wavFile.name, "audio/wav") { status ->
                            _uiState.value = AiMusicUiState.Processing(ProcessingStep.ANALYZING, status)
                        }
                    }
                } finally {
                    wavFile.delete()
                }

                _uiState.value = AiMusicUiState.Processing(ProcessingStep.COMPLETE)
                _uiState.value = AiMusicUiState.Success(result)
                saveToHistory(result, "microphone", "Mikrofon Kaydı")

            } catch (e: CancellationException) {
                amplitudeJob.cancel()
                elapsedJob.cancel()
                throw e
            } catch (e: Exception) {
                amplitudeJob.cancel()
                elapsedJob.cancel()
                _uiState.value = AiMusicUiState.Error(mapErrorMessage(e))
            }
        }
    }

    fun stopRecording() {
        audioRecorder.requestStop()
    }

    // ── History ────────────────────────────────

    private fun saveToHistory(result: AnalysisResult, sourceType: String, sourceName: String) {
        viewModelScope.launch {
            try {
                historyUseCase.saveResult(result, sourceType, sourceName)
            } catch (_: Exception) {
                // Don't fail the UI if history save fails
            }
        }
    }

    // ── Utilities ──────────────────────────────

    private fun detectSourceLabel(url: String): String {
        val lower = url.lowercase()
        return when {
            "tiktok.com" in lower -> "tiktok"
            "instagram.com" in lower -> "instagram"
            "soundcloud.com" in lower -> "soundcloud"
            "twitter.com" in lower || "x.com" in lower -> "twitter"
            else -> "youtube"
        }
    }

    private fun isValidMediaUrl(url: String): Boolean {
        val patterns = listOf(
            // YouTube
            "youtube.com/watch", "youtu.be/", "youtube.com/v/", "youtube.com/embed/",
            // TikTok
            "tiktok.com/", "vm.tiktok.com/",
            // Instagram
            "instagram.com/reel", "instagram.com/p/",
            // SoundCloud
            "soundcloud.com/",
            // Twitter/X
            "twitter.com/", "x.com/"
        )
        return patterns.any { url.contains(it, ignoreCase = true) }
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
                "Geçersiz URL"
            e.message?.contains("youtube_analysis_failed") == true ->
                "Analiz başarısız oldu. URL'yi kontrol edin."
            e.message?.contains("Mikrofon") == true ->
                "Mikrofon erişimi sağlanamadı. İzinleri kontrol edin."
            e.message?.contains("Unable to resolve host") == true ||
            e.message?.contains("Failed to connect") == true ->
                "Sunucuya bağlanılamadı. İnternet bağlantınızı kontrol edin."
            e.message?.contains("timeout") == true ->
                "İstek zaman aşımına uğradı. Tekrar deneyin."
            else ->
                e.message ?: "Bilinmeyen bir hata oluştu"
        }
    }

    fun reset() {
        recordingJob?.cancel()
        recordingJob = null
        analysisJob?.cancel()
        analysisJob = null
        _uiState.value = AiMusicUiState.Idle
        selectedFileUri = null
    }
}
