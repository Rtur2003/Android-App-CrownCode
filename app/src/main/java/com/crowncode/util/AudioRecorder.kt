package com.crowncode.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRecorder @Inject constructor() {

    companion object {
        const val SAMPLE_RATE = 16_000
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val MAX_DURATION_SEC = 30
    }

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    @Volatile
    private var stopRequested = false

    fun requestStop() {
        stopRequested = true
    }

    /**
     * Records audio from microphone and saves as WAV file.
     * Runs on IO dispatcher. Cancellation-safe via coroutine isActive check
     * and gracefully completes on [requestStop] so captured audio can be uploaded.
     */
    suspend fun record(context: Context): File = withContext(Dispatchers.IO) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Mikrofon izni verilmedi")
        }
        stopRequested = false
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            .coerceAtLeast(4096)

        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )

        check(recorder.state == AudioRecord.STATE_INITIALIZED) {
            "Mikrofon başlatılamadı"
        }

        val pcmStream = ByteArrayOutputStream()
        val buffer = ShortArray(bufferSize / 2)
        val maxSamples = SAMPLE_RATE * MAX_DURATION_SEC
        var totalSamples = 0

        try {
            recorder.startRecording()
            _elapsedSeconds.value = 0

            while (isActive && !stopRequested && totalSamples < maxSamples) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    // Write PCM data
                    val byteBuffer = ByteBuffer.allocate(read * 2)
                    byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                    for (i in 0 until read) {
                        byteBuffer.putShort(buffer[i])
                    }
                    pcmStream.write(byteBuffer.array())
                    totalSamples += read

                    // Calculate amplitude for visualization
                    var sum = 0L
                    for (i in 0 until read) {
                        sum += buffer[i] * buffer[i]
                    }
                    val rms = Math.sqrt(sum.toDouble() / read).toFloat()
                    _amplitude.value = (rms / Short.MAX_VALUE).coerceIn(0f, 1f)

                    _elapsedSeconds.value = totalSamples / SAMPLE_RATE
                }
            }
        } finally {
            recorder.stop()
            recorder.release()
            _amplitude.value = 0f
        }

        // Write WAV file
        val pcmData = pcmStream.toByteArray()
        val wavFile = File(context.cacheDir, "recording_${System.currentTimeMillis()}.wav")
        FileOutputStream(wavFile).use { fos ->
            writeWavHeader(fos, pcmData.size, SAMPLE_RATE, 1, 16)
            fos.write(pcmData)
        }

        wavFile
    }

    private fun writeWavHeader(
        out: FileOutputStream,
        pcmDataSize: Int,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val totalDataLen = pcmDataSize + 36

        val header = ByteBuffer.allocate(44)
        header.order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        header.put("RIFF".toByteArray())
        header.putInt(totalDataLen)
        header.put("WAVE".toByteArray())

        // fmt chunk
        header.put("fmt ".toByteArray())
        header.putInt(16) // chunk size
        header.putShort(1) // PCM format
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(blockAlign.toShort())
        header.putShort(bitsPerSample.toShort())

        // data chunk
        header.put("data".toByteArray())
        header.putInt(pcmDataSize)

        out.write(header.array())
    }
}
