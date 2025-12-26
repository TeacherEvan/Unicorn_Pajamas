package com.teacherevan.unicornpajamas

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Service for generating and playing voice audio using ElevenLabs API
 */
class VoiceService(private val context: Context) {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private var mediaPlayer: MediaPlayer? = null
    
    companion object {
        private const val TAG = "VoiceService"
        private const val API_BASE_URL = "https://api.elevenlabs.io/v1"
        
        // Male voice IDs - stoic and gentle
        const val VOICE_LUCA = "4JVOFy4SLQs9my0OLhEw"  // Calm, sober, gentle
        const val VOICE_AJ = "OtD5NgAuh1zlbur5wvSw"    // Calm, steady, measured
        const val VOICE_GEORGE = "JBFqnCBsd6RMkjVDRZzb" // British, warm, gentle
        
        // Female voice IDs - for the first voice
        const val VOICE_RACHEL = "21m00Tcm4TlvDq8ikWAM" // Clear, friendly female
        const val VOICE_BELLA = "EXAVITQu4vr4xnSDxMaL"  // Soft, young female
    }
    
    /**
     * Generate speech from text using ElevenLabs API
     * @param text The text to convert to speech
     * @param voiceId The voice ID to use
     * @param apiKey The ElevenLabs API key
     * @return File containing the generated audio, or null on error
     */
    suspend fun generateSpeech(text: String, voiceId: String, apiKey: String): File? = withContext(Dispatchers.IO) {
        try {
            val url = "$API_BASE_URL/text-to-speech/$voiceId"
            
            // Build request body with optimal settings for stoic/gentle delivery
            val jsonBody = JSONObject().apply {
                put("text", text)
                put("model_id", "eleven_monolingual_v1")
                put("voice_settings", JSONObject().apply {
                    put("stability", 0.7)        // More stable, less variation
                    put("similarity_boost", 0.7)  // Natural sounding
                    put("style", 0.2)             // Subtle delivery for stoic tone
                    put("use_speaker_boost", true)
                })
            }
            
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaType())
            
            val request = Request.Builder()
                .url(url)
                .addHeader("xi-api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "audio/mpeg")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e(TAG, "API request failed: ${response.code} - ${response.message}")
                Log.e(TAG, "Response body: ${response.body?.string()}")
                return@withContext null
            }
            
            // Save audio to cache directory
            val audioFile = File(context.cacheDir, "voice_${System.currentTimeMillis()}.mp3")
            response.body?.byteStream()?.use { input ->
                audioFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            
            Log.d(TAG, "Speech generated successfully: ${audioFile.absolutePath}")
            audioFile
        } catch (e: IOException) {
            Log.e(TAG, "Error generating speech", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error generating speech", e)
            null
        }
    }
    
    /**
     * Play an audio file
     * @param audioFile The audio file to play
     * @param onComplete Callback when playback completes
     */
    suspend fun playAudio(audioFile: File, onComplete: (() -> Unit)? = null) = withContext(Dispatchers.Main) {
        try {
            // Release any existing player
            releasePlayer()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                setOnCompletionListener {
                    Log.d(TAG, "Audio playback completed")
                    releasePlayer()
                    onComplete?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    releasePlayer()
                    false
                }
                prepare()
                start()
            }
            
            Log.d(TAG, "Started playing audio: ${audioFile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio", e)
            onComplete?.invoke()
        }
    }
    
    /**
     * Generate and play speech
     * @param text The text to convert to speech
     * @param voiceId The voice ID to use
     * @param apiKey The ElevenLabs API key
     * @param onComplete Callback when playback completes
     */
    suspend fun speak(text: String, voiceId: String, apiKey: String, onComplete: (() -> Unit)? = null) {
        val audioFile = generateSpeech(text, voiceId, apiKey)
        if (audioFile != null) {
            playAudio(audioFile, onComplete)
        } else {
            Log.e(TAG, "Failed to generate speech for: $text")
            onComplete?.invoke()
        }
    }
    
    /**
     * Play welcome sequence: female voice followed by male voice
     * @param apiKey The ElevenLabs API key
     * @param onComplete Callback when entire sequence completes
     */
    suspend fun playWelcomeSequence(apiKey: String, onComplete: (() -> Unit)? = null) {
        // First, play female voice with a welcome message
        speak(
            text = "Welcome to Unicorn Pajamas!",
            voiceId = VOICE_RACHEL,
            apiKey = apiKey
        ) {
            // After female voice completes, play male voice
            kotlinx.coroutines.GlobalScope.launch(Dispatchers.Main) {
                speak(
                    text = "Learning through games for everyone.",
                    voiceId = VOICE_LUCA,  // Calm, gentle male voice
                    apiKey = apiKey,
                    onComplete = onComplete
                )
            }
        }
    }
    
    /**
     * Release media player resources
     */
    private fun releasePlayer() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        releasePlayer()
    }
}
