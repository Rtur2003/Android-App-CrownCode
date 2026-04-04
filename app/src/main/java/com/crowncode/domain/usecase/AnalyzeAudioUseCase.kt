package com.crowncode.domain.usecase

import com.crowncode.domain.model.AnalysisResult
import com.crowncode.domain.repository.AnalysisRepository
import java.io.InputStream
import javax.inject.Inject

class AnalyzeAudioUseCase @Inject constructor(
    private val repository: AnalysisRepository
) {
    suspend fun analyzeUrl(url: String): AnalysisResult {
        val sourceType = detectSourceType(url)
        return repository.analyzeUrl(url, sourceType)
    }

    suspend fun analyzeFile(
        inputStream: InputStream,
        fileName: String,
        mimeType: String
    ): AnalysisResult {
        return repository.analyzeFile(inputStream, fileName, mimeType)
    }

    private fun detectSourceType(url: String): String {
        val lower = url.lowercase()
        return when {
            "tiktok.com" in lower || "vm.tiktok.com" in lower -> "tiktok"
            "instagram.com" in lower -> "instagram"
            "soundcloud.com" in lower -> "soundcloud"
            "twitter.com" in lower || "x.com" in lower -> "twitter"
            else -> "youtube"
        }
    }
}
