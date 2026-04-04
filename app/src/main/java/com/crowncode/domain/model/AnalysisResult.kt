package com.crowncode.domain.model

data class AnalysisResult(
    val isAiGenerated: Boolean,
    val confidence: Float,
    val processingTime: Float,
    val modelVersion: String,
    val decisionSource: String,
    val analysisMode: String,
    val audioInfo: AudioInfo,
    val features: AnalysisFeatures,
    val vocalAnalysis: VocalAnalysis? = null,
    val warnings: List<String> = emptyList()
)

data class AudioInfo(
    val duration: Float,
    val sampleRate: Int,
    val format: String
)

data class AnalysisFeatures(
    val spectralRegularity: Float,
    val temporalPatterns: Float,
    val harmonicStructure: Float,
    val indicators: List<String>
)

data class VocalAnalysis(
    val hasVocals: Boolean,
    val vocalAiScore: Float,
    val pitchStabilityScore: Float,
    val vibratoRegularityScore: Float,
    val formantConsistencyScore: Float,
    val breathPatternScore: Float,
    val indicators: List<String>
)
