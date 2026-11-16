package com.crowncode

import com.crowncode.data.remote.dto.AnalysisJobDto
import com.crowncode.util.AudioFileResolver
import com.crowncode.util.ValidationUtils
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyzeContractTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun `ensureAudioExtension preserves valid audio extensions`() {
        assertEquals("song.wav", AudioFileResolver.ensureAudioExtension("song.wav", "audio/wav"))
        assertEquals("demo.flac", AudioFileResolver.ensureAudioExtension("demo.flac", null))
        assertEquals("beat.m4a", AudioFileResolver.ensureAudioExtension("beat.m4a", "audio/mp4"))
    }

    @Test
    fun `ensureAudioExtension infers extension for Android SAF URIs without extension`() {
        assertEquals("audio_12345.mp3", AudioFileResolver.ensureAudioExtension("audio:12345", "audio/mpeg"))
        assertEquals("recording.wav", AudioFileResolver.ensureAudioExtension("recording", "audio/wav"))
        assertEquals("clip.webm", AudioFileResolver.ensureAudioExtension("clip", "video/webm; codecs=opus"))
        assertEquals("audio_file.ogg", AudioFileResolver.ensureAudioExtension(null, "audio/ogg"))
    }

    @Test
    fun `deserializes background analysis job with xai and meta classifier`() {
        val raw = """
            {
              "jobId": "job-abc123",
              "sourceType": "file",
              "status": "done",
              "phase": "done",
              "elapsedSec": 4.2,
              "steps": [
                {"id": "features", "state": "done", "seconds": 0.4},
                {"id": "xai", "state": "done", "seconds": 0.1}
              ],
              "response": {
                "result": {
                  "isAIGenerated": false,
                  "confidence": 0.18,
                  "processingTime": 4.2,
                  "modelVersion": "auris-v1-fusion",
                  "decisionSource": "auris_xai_lightgbm",
                  "analysisMode": "production",
                  "source": {"kind": "file", "fileName": "demo.mp3", "fileSizeBytes": 12345, "mimeType": "audio/mpeg"},
                  "features": {"spectralRegularity": 0.31, "temporalPatterns": 0.28, "harmonicStructure": 0.42, "artificialIndicators": []},
                  "audioInfo": {"duration": 12.0, "analysedSec": 12.0, "sampleRate": 44100, "bitrate": 320, "format": "mp3", "channels": 2},
                  "towerScores": {"local_features": 0.34, "xai_ensemble": 0.18},
                  "xai": {
                    "probability": 0.18,
                    "threshold": 0.4316,
                    "baseProbability": 0.5,
                    "confidenceBand": {"tier": "strong", "labelTr": "Güçlü", "labelEn": "Strong", "margin": 0.58},
                    "modelVotes": [
                      {"name": "LightGBM", "probability": 0.18, "vote": "human", "available": true},
                      {"name": "XGBoost", "probability": 0.21, "vote": "human", "available": true}
                    ],
                    "bestModel": "LightGBM",
                    "topContributions": [
                      {"name": "rms_energy", "label": "RMS Enerji", "labelEn": "RMS Energy", "category": "temporal", "value": 0.14, "zScore": -0.4, "shapValue": -0.22, "direction": "towards_human", "description": "Natural dynamic range"}
                    ],
                    "featureCount": 47
                  },
                  "metaClassifier": {
                    "isAIGenerated": false,
                    "confidence": 0.84,
                    "modelVersion": "trained-lgbm-v1",
                    "towerScores": {"wav2vec2": 0.19},
                    "indicators": []
                  }
                },
                "warnings": [],
                "errors": []
              }
            }
        """.trimIndent()

        val parsed = json.decodeFromString<AnalysisJobDto>(raw)
        assertEquals("done", parsed.status)
        val res = parsed.response?.result
        assertNotNull(res)
        assertFalse(res!!.isAIGenerated)
        assertEquals(0.18f, res.confidence, 0.0001f)
        assertEquals("Güçlü", res.xai?.confidenceBand?.labelTr)
        assertEquals(2, res.xai?.modelVotes?.size)
        assertEquals(47, res.xai?.featureCount)
        assertEquals("trained-lgbm-v1", res.metaClassifier?.modelVersion)
    }

    @Test
    fun `validation utils enforces minimum name and password constraints`() {
        assertTrue(ValidationUtils.isValidName("Talia"))
        assertFalse(ValidationUtils.isValidName(" "))
        assertTrue(ValidationUtils.isValidPassword("secret99"))
        assertFalse(ValidationUtils.isValidPassword("123"))
    }
}
