package com.crowncode.domain.usecase

import com.crowncode.data.local.dao.AnalysisHistoryDao
import com.crowncode.data.local.entity.AnalysisHistoryEntity
import com.crowncode.domain.model.AnalysisResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnalysisHistoryUseCase @Inject constructor(
    private val dao: AnalysisHistoryDao
) {
    fun getRecentHistory(limit: Int = 20): Flow<List<AnalysisHistoryEntity>> {
        return dao.getRecent(limit)
    }

    fun getAllHistory(): Flow<List<AnalysisHistoryEntity>> {
        return dao.getAll()
    }

    suspend fun saveResult(result: AnalysisResult, sourceType: String, sourceName: String) {
        dao.insert(
            AnalysisHistoryEntity(
                sourceType = sourceType,
                sourceName = sourceName,
                isAiGenerated = result.isAiGenerated,
                confidence = result.confidence,
                processingTime = result.processingTime,
                modelVersion = result.modelVersion,
                decisionSource = result.decisionSource,
                analysisMode = result.analysisMode,
                duration = result.audioInfo.duration,
                sampleRate = result.audioInfo.sampleRate,
                format = result.audioInfo.format,
                spectralRegularity = result.features.spectralRegularity,
                temporalPatterns = result.features.temporalPatterns,
                harmonicStructure = result.features.harmonicStructure,
                hasVocals = result.vocalAnalysis?.hasVocals ?: false,
                vocalAiScore = result.vocalAnalysis?.vocalAiScore ?: 0f,
                indicators = result.features.indicators.joinToString(",")
            )
        )
    }

    suspend fun deleteEntry(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.deleteAll()
    }
}
