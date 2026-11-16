package com.crowncode.data.repository

import com.crowncode.data.remote.api.AnalyzeApi
import com.crowncode.data.remote.dto.AnalysisJobDto
import com.crowncode.data.remote.dto.AnalyzeResponseDto
import com.crowncode.domain.model.AnalysisFeatures
import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.model.AudioInfo
import com.crowncode.domain.model.ConfidenceBand
import com.crowncode.domain.model.FeatureContribution
import com.crowncode.domain.model.MetaClassifierInfo
import com.crowncode.domain.model.ModelVote
import com.crowncode.domain.model.VocalAnalysis
import com.crowncode.domain.model.XaiExplanation
import com.crowncode.domain.repository.AnalysisRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.InputStream
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val api: AnalyzeApi
) : AnalysisRepository {

    companion object {
        private const val POLL_INTERVAL_MS = 1500L
        private const val MAX_POLLS = 180 // ~4.5 minutes max for long queued audio
    }

    override suspend fun analyzeUrl(
        url: String,
        sourceType: String,
        onStatus: (String) -> Unit
    ): AnalysisResult {
        val sourceTypeBody = sourceType.toRequestBody("text/plain".toMediaTypeOrNull())
        val urlBody = url.toRequestBody("text/plain".toMediaTypeOrNull())

        return executeJobOrFallback(
            startJob = { api.startUrlJob(sourceTypeBody, urlBody) },
            fallbackSync = { api.analyzeUrl(sourceTypeBody, urlBody) },
            onStatus = onStatus
        )
    }

    override suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String,
        onStatus: (String) -> Unit
    ): AnalysisResult {
        val sourceType = "file".toRequestBody("text/plain".toMediaTypeOrNull())
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)

        return executeJobOrFallback(
            startJob = { api.startFileJob(sourceType, filePart) },
            fallbackSync = { api.analyzeFile(sourceType, filePart) },
            onStatus = onStatus
        )
    }

    private suspend fun executeJobOrFallback(
        startJob: suspend () -> AnalysisJobDto,
        fallbackSync: suspend () -> AnalyzeResponseDto,
        onStatus: (String) -> Unit
    ): AnalysisResult {
        val initialJob = try {
            startJob()
        } catch (e: HttpException) {
            if (e.code() == 404 || e.code() == 405) {
                onStatus("Analiz çalıştırılıyor...")
                return fallbackSync().toDomain()
            }
            throw e
        }

        if (initialJob.status in listOf("done", "error") && initialJob.response != null) {
            return initialJob.response.toDomain()
        }

        val jobId = initialJob.jobId
            ?: return (initialJob.response ?: throw AnalysisException("İş başlatılamadı")).toDomain()

        return pollJob(jobId, initialJob, onStatus)
    }

    private suspend fun pollJob(
        jobId: String,
        firstSnapshot: AnalysisJobDto,
        onStatus: (String) -> Unit
    ): AnalysisResult {
        var current = firstSnapshot
        onStatus(formatJobStatus(current))

        try {
            repeat(MAX_POLLS) {
                if (current.status == "done" || current.status == "error") {
                    val finalResponse = current.response
                        ?: throw AnalysisException("Analiz yanıtı boş döndü")
                    return finalResponse.toDomain()
                }
                delay(POLL_INTERVAL_MS)
                current = api.getJob(jobId)
                onStatus(formatJobStatus(current))
            }
            throw AnalysisException("Analiz zaman aşımına uğradı")
        } catch (ce: CancellationException) {
            withContext(NonCancellable) {
                runCatching { api.cancelJob(jobId) }
            }
            throw ce
        }
    }

    private fun formatJobStatus(job: AnalysisJobDto): String {
        val queue = job.queue
        if (job.phase == "queued" && queue != null && queue.position > 0) {
            return "Sırada bekleniyor (#${queue.position})..."
        }
        val running = job.steps.firstOrNull { it.state == "running" }
        return when (running?.id) {
            "download" -> "Ses indiriliyor..."
            "features" -> "Akustik öznitelikler çıkarılıyor..."
            "vocals" -> "Vokal katmanı taranıyor..."
            "wav2vec2" -> "Wav2Vec2 derin ses modeli çalışıyor..."
            "clap" -> "CLAP ses gömmeleri analiz ediliyor..."
            "timeline" -> running.progress?.let { p ->
                "Zaman çizelgesi taranıyor (%${(p * 100).toInt()})..."
            } ?: "Parça zaman çizelgesi taranıyor..."
            "fst" -> "FST modeli değerlendiriyor..."
            "xai" -> "XAI açıklanabilirlik hesaplanıyor..."
            "meta" -> "LightGBM meta-sınıflandırıcı doğruluyor..."
            else -> "AURIS çoklu model pipeline çalışıyor..."
        }
    }

    private fun AnalyzeResponseDto.toDomain(): AnalysisResult {
        if (errors.isNotEmpty() && result == null) {
            throw AnalysisException(errors.joinToString(", "))
        }

        val r = result ?: throw AnalysisException("Sunucudan sonuç alınamadı")

        return AnalysisResult(
            isAiGenerated = r.isAIGenerated,
            confidence = r.confidence,
            processingTime = r.processingTime,
            modelVersion = r.modelVersion,
            decisionSource = r.decisionSource,
            analysisMode = r.analysisMode,
            audioInfo = AudioInfo(
                duration = r.audioInfo.duration,
                sampleRate = r.audioInfo.sampleRate,
                format = r.audioInfo.format
            ),
            features = AnalysisFeatures(
                spectralRegularity = r.features.spectralRegularity,
                temporalPatterns = r.features.temporalPatterns,
                harmonicStructure = r.features.harmonicStructure,
                indicators = r.features.artificialIndicators
            ),
            vocalAnalysis = r.vocalAnalysis?.let { v ->
                if (v.hasVocals) VocalAnalysis(
                    hasVocals = true,
                    vocalAiScore = v.vocalAiScore,
                    pitchStabilityScore = v.pitchStabilityScore,
                    vibratoRegularityScore = v.vibratoRegularityScore,
                    formantConsistencyScore = v.formantConsistencyScore,
                    breathPatternScore = v.breathPatternScore,
                    indicators = v.indicators
                ) else null
            },
            xai = r.xai?.let { x ->
                XaiExplanation(
                    probability = x.probability,
                    threshold = x.threshold,
                    confidenceBand = x.confidenceBand?.let { b ->
                        ConfidenceBand(
                            tier = b.tier,
                            labelTr = b.labelTr,
                            labelEn = b.labelEn,
                            margin = b.margin
                        )
                    },
                    modelVotes = x.modelVotes.map { mv ->
                        ModelVote(
                            name = mv.name,
                            probability = mv.probability,
                            vote = mv.vote,
                            available = mv.available
                        )
                    },
                    bestModel = x.bestModel,
                    topContributions = x.topContributions.map { c ->
                        FeatureContribution(
                            name = c.name,
                            label = c.label,
                            value = c.value,
                            shapValue = c.shapValue,
                            direction = c.direction
                        )
                    },
                    featureCount = x.featureCount
                )
            },
            towerScores = r.towerScores,
            metaClassifier = r.metaClassifier?.let { m ->
                MetaClassifierInfo(
                    isAiGenerated = m.isAIGenerated,
                    confidence = m.confidence,
                    modelVersion = m.modelVersion,
                    indicators = m.indicators
                )
            },
            warnings = warnings
        )
    }
}

class AnalysisException(message: String) : Exception(message)
