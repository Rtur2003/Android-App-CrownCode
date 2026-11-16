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
    val xai: XaiExplanation? = null,
    val towerScores: Map<String, Float> = emptyMap(),
    val metaClassifier: MetaClassifierInfo? = null,
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

data class ConfidenceBand(
    val tier: String,
    val labelTr: String,
    val labelEn: String,
    val margin: Float
)

data class ModelVote(
    val name: String,
    val probability: Float,
    val vote: String,
    val available: Boolean
)

data class FeatureContribution(
    val name: String,
    val label: String,
    val value: Float,
    val shapValue: Float,
    val direction: String
)

data class XaiExplanation(
    val probability: Float,
    val threshold: Float,
    val confidenceBand: ConfidenceBand?,
    val modelVotes: List<ModelVote>,
    val bestModel: String,
    val topContributions: List<FeatureContribution>,
    val featureCount: Int
)

data class MetaClassifierInfo(
    val isAiGenerated: Boolean,
    val confidence: Float,
    val modelVersion: String,
    val indicators: List<String>
)
