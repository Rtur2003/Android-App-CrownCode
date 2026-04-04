package com.crowncode.domain.usecase

import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.repository.AnalysisRepository
import java.io.InputStream
import javax.inject.Inject

class AnalyzeAudioUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    suspend fun analyzeUrl(url: String): AnalysisResult {
        return repository.analyzeYoutubeUrl(url)
    }

    suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): AnalysisResult {
        return repository.analyzeFile(inputStream, fileName, mimeType)
    }
}
