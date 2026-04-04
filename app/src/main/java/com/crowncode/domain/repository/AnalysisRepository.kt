package com.crowncode.domain.repository

import com.crowncode.domain.model.AnalysisResult
import java.io.InputStream

interface AnalysisRepository {
    suspend fun analyzeYoutubeUrl(url: String): AnalysisResult
    suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): AnalysisResult
}
