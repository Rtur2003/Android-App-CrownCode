package com.crowncode.util

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Resolves a human-readable upload filename and guarantees a supported audio extension
 * so SAF content:// URIs never reach the backend without a valid extension.
 */
object AudioFileResolver {

    private val SUPPORTED_EXTENSIONS = setOf(
        "mp3", "wav", "flac", "ogg", "m4a", "aac", "opus", "webm", "wma"
    )

    private val MIME_TO_EXTENSION = mapOf(
        "audio/mpeg" to "mp3",
        "audio/mp3" to "mp3",
        "audio/wav" to "wav",
        "audio/x-wav" to "wav",
        "audio/wave" to "wav",
        "audio/flac" to "flac",
        "audio/x-flac" to "flac",
        "audio/mp4" to "m4a",
        "audio/m4a" to "m4a",
        "audio/x-m4a" to "m4a",
        "audio/aac" to "aac",
        "audio/ogg" to "ogg",
        "audio/opus" to "opus",
        "audio/webm" to "webm",
        "video/webm" to "webm"
    )

    fun resolveDisplayName(contentResolver: ContentResolver, uri: Uri): String {
        val rawName = runCatching {
            contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (idx >= 0 && cursor.moveToFirst()) cursor.getString(idx) else null
            }
        }.getOrNull() ?: uri.lastPathSegment?.substringAfterLast('/')

        val mimeType = runCatching { contentResolver.getType(uri) }.getOrNull()
        return ensureAudioExtension(rawName, mimeType)
    }

    fun ensureAudioExtension(rawName: String?, mimeType: String?): String {
        val clean = rawName
            ?.trim()
            ?.replace(Regex("""[\\/:*?"<>|]+"""), "_")
            ?.takeIf { it.isNotEmpty() }
            ?: "audio_file"

        val currentExt = clean.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        if (currentExt in SUPPORTED_EXTENSIONS) {
            return clean
        }

        val normalizedMime = mimeType?.substringBefore(';')?.trim()?.lowercase()
        val inferredExt = MIME_TO_EXTENSION[normalizedMime] ?: "mp3"
        val base = clean.substringBeforeLast('.', missingDelimiterValue = clean)
            .ifBlank { "audio_file" }
        return "$base.$inferredExt"
    }
}
