package com.crowncode.domain.repository

import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.model.ServerHealthState
import java.io.InputStream

interface AnalysisRepository {
    suspend fun checkHealth(): ServerHealthState

    suspend fun analyzeUrl(
        url: String,
        sourceType: String,
        onStatus: (String) -> Unit = {}
    ): AnalysisResult

    suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String,
        onStatus: (String) -> Unit = {}
    ): AnalysisResult
}
