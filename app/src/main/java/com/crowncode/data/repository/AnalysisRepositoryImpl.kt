package com.crowncode.data.repository

import com.crowncode.data.remote.api.AnalyzeApi
import com.crowncode.data.remote.dto.AnalyzeResponseDto
import com.crowncode.domain.model.AnalysisFeatures
import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.model.AudioInfo
import com.crowncode.domain.model.VocalAnalysis
import com.crowncode.domain.repository.AnalysisRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val api: AnalyzeApi
) : AnalysisRepository {

    override suspend fun analyzeUrl(url: String, sourceType: String): AnalysisResult {
        val sourceTypeBody = sourceType.toRequestBody("text/plain".toMediaTypeOrNull())
        val urlBody = url.toRequestBody("text/plain".toMediaTypeOrNull())
        val response = api.analyzeUrl(sourceTypeBody, urlBody)
        return response.toDomain()
    }

    override suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): AnalysisResult {
        val sourceType = "file".toRequestBody("text/plain".toMediaTypeOrNull())
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestBody)
        val response = api.analyzeFile(sourceType, filePart)
        return response.toDomain()
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
            warnings = warnings
        )
    }
}

class AnalysisException(message: String) : Exception(message)
