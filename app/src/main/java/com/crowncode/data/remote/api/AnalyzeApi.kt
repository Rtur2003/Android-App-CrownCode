package com.crowncode.data.remote.api

import com.crowncode.data.remote.dto.AnalyzeResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AnalyzeApi {

    @Multipart
    @POST("api/analyze")
    suspend fun analyzeUrl(
        @Part("sourceType") sourceType: RequestBody,
        @Part("url") url: RequestBody
    ): AnalyzeResponseDto

    @Multipart
    @POST("api/analyze")
    suspend fun analyzeFile(
        @Part("sourceType") sourceType: RequestBody,
        @Part file: MultipartBody.Part
    ): AnalyzeResponseDto
}
