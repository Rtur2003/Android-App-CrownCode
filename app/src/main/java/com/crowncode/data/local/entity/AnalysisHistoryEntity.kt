package com.crowncode.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "analysis_history")
data class AnalysisHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceType: String, // "file", "youtube", "tiktok", "microphone"
    val sourceName: String, // filename, URL, or "Mikrofon Kaydı"
    val isAiGenerated: Boolean,
    val confidence: Float,
    val processingTime: Float,
    val modelVersion: String,
    val decisionSource: String,
    val analysisMode: String,
    val duration: Float,
    val sampleRate: Int,
    val format: String,
    val spectralRegularity: Float,
    val temporalPatterns: Float,
    val harmonicStructure: Float,
    val hasVocals: Boolean = false,
    val vocalAiScore: Float = 0f,
    val indicators: String = "" // comma-separated
)
