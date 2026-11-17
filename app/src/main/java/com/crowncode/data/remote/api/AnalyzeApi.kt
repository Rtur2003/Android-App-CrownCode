package com.crowncode.data.remote.api

import com.crowncode.data.remote.dto.AnalysisJobDto
import com.crowncode.data.remote.dto.AnalyzeResponseDto
import com.crowncode.data.remote.dto.HealthResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AnalyzeApi {

    @GET("api/health")
    suspend fun getHealth(): HealthResponseDto

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

    @Multipart
    @POST("api/analyze/jobs")
    suspend fun startUrlJob(
        @Part("sourceType") sourceType: RequestBody,
        @Part("url") url: RequestBody
    ): AnalysisJobDto

    @Multipart
    @POST("api/analyze/jobs")
    suspend fun startFileJob(
        @Part("sourceType") sourceType: RequestBody,
        @Part file: MultipartBody.Part
    ): AnalysisJobDto

    @GET("api/analyze/jobs/{jobId}")
    suspend fun getJob(@Path("jobId") jobId: String): AnalysisJobDto

    @DELETE("api/analyze/jobs/{jobId}")
    suspend fun cancelJob(@Path("jobId") jobId: String): AnalysisJobDto
}
