package com.crowncode.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisJobDto(
    val jobId: String? = null,
    val sourceType: String = "file",
    val status: String = "running", // running | done | error
    val phase: String? = null,      // queued | running | done | error
    val steps: List<JobStepDto> = emptyList(),
    val elapsedSec: Float = 0f,
    val queue: JobQueueDto? = null,
    val response: AnalyzeResponseDto? = null
)

@Serializable
data class JobStepDto(
    val id: String,
    val state: String, // pending | running | done | failed | skipped
    val seconds: Float? = null,
    val progress: Float? = null
)

@Serializable
data class JobQueueDto(
    val position: Int = 0,
    val ahead: Int = 0,
    val estimatedWaitSec: Float? = null
)

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
    val vocalAnalysis: VocalAnalysisDto? = null,
    val xai: XaiExplanationDto? = null,
    val towerScores: Map<String, Float> = emptyMap(),
    val metaClassifier: MetaClassifierDto? = null
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
    val analysedSec: Float? = null,
    val sampleRate: Int = 44100,
    val bitrate: Int = 128,
    val format: String = "unknown",
    val channels: Int = 0
)

@Serializable
data class ConfidenceBandDto(
    val tier: String = "likely",
    val labelTr: String = "",
    val labelEn: String = "",
    val margin: Float = 0f
)

@Serializable
data class ModelVoteDto(
    val name: String,
    val probability: Float,
    val vote: String,
    val available: Boolean = true
)

@Serializable
data class FeatureContributionDto(
    val name: String,
    val label: String,
    val labelEn: String = "",
    val category: String = "other",
    val value: Float = 0f,
    val zScore: Float = 0f,
    val shapValue: Float = 0f,
    val direction: String = "neutral",
    val description: String = ""
)

@Serializable
data class XaiExplanationDto(
    val probability: Float,
    val threshold: Float = 0.4316f,
    val baseProbability: Float = 0.5f,
    val confidenceBand: ConfidenceBandDto? = null,
    val modelVotes: List<ModelVoteDto> = emptyList(),
    val bestModel: String = "LightGBM",
    val topContributions: List<FeatureContributionDto> = emptyList(),
    val featureCount: Int = 0
)

@Serializable
data class MetaClassifierDto(
    val isAIGenerated: Boolean,
    val confidence: Float,
    val modelVersion: String,
    val towerScores: Map<String, Float> = emptyMap(),
    val indicators: List<String> = emptyList()
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
