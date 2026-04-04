package com.crowncode.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeResponseDto(
    val result: AnalysisResultDto? = null,
    val warnings: List<String> = emptyList(),
    val errors: List<String> = emptyList()
)

@Serializable
data class AnalysisResultDto(
    val isAIGenerated: Boolean,
    val confidence: Float,
    val processingTime: Float,
    val modelVersion: String,
    val decisionSource: String,
    val analysisMode: String,
    val source: SourceDto,
    val features: AnalysisFeaturesDto,
    val audioInfo: AudioInfoDto,
    val vocalAnalysis: VocalAnalysisDto? = null
)

@Serializable
data class SourceDto(
    val kind: String,
    val url: String? = null,
    val normalizedUrl: String? = null,
    val videoId: String? = null,
    val startTimeSec: Float? = null,
    val fileName: String? = null,
    val fileSizeBytes: Long? = null,
    val mimeType: String? = null
)

@Serializable
data class AnalysisFeaturesDto(
    val spectralRegularity: Float = 0.5f,
    val temporalPatterns: Float = 0.5f,
    val harmonicStructure: Float = 0.5f,
    val artificialIndicators: List<String> = emptyList()
)

@Serializable
data class AudioInfoDto(
    val duration: Float = 0f,
    val sampleRate: Int = 44100,
    val bitrate: Int = 128,
    val format: String = "unknown"
)

@Serializable
data class VocalAnalysisDto(
    val hasVocals: Boolean = false,
    val vocalConfidence: Float = 0f,
    val vocalAiScore: Float = 0f,
    val pitchStabilityScore: Float = 0f,
    val vibratoRegularityScore: Float = 0f,
    val formantConsistencyScore: Float = 0f,
    val breathPatternScore: Float = 0f,
    val vocalTextureScore: Float = 0f,
    val pitchMeanHz: Float = 0f,
    val pitchStdCents: Float = 0f,
    val vibratoRateHz: Float = 0f,
    val vibratoExtentCents: Float = 0f,
    val indicators: List<String> = emptyList()
)
