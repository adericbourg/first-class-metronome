package dev.dericbourg.firstclassmetronome.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.HandlerThread
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.dericbourg.firstclassmetronome.R
import dev.dericbourg.firstclassmetronome.data.settings.HapticStrength
import dev.dericbourg.firstclassmetronome.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetronomePlayer @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val vibrator: Vibrator,
    private val settingsRepository: SettingsRepository
) {
    private val isPlaying = AtomicBoolean(false)
    private val currentBpm = AtomicInteger(60)
    private val hapticEnabled = AtomicBoolean(false)
    private val hapticStrength = AtomicReference(HapticStrength.MEDIUM)
    private var playbackThread: Thread? = null
    private var clickSamples: ShortArray? = null

    val playing: Boolean
        get() = isPlaying.get()

    init {
        loadClickSound()
    }

    private fun loadClickSound() {
        try {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.click)
            val bytes = inputStream.readBytes()
            inputStream.close()

            // Skip WAV header (44 bytes) and convert to ShortArray
            val dataBytes = bytes.copyOfRange(WAV_HEADER_SIZE, bytes.size)
            clickSamples = ShortArray(dataBytes.size / 2) { i ->
                ((dataBytes[i * 2 + 1].toInt() shl 8) or (dataBytes[i * 2].toInt() and 0xFF)).toShort()
            }

            Log.d(TAG, "Click sound loaded: ${clickSamples?.size} samples")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load click sound", e)
        }
    }

    fun start(bpm: Int) {
        if (isPlaying.get()) return

        currentBpm.set(bpm)

        val samples = clickSamples
        if (samples == null) {
            Log.e(TAG, "Cannot start: click sound not loaded")
            return
        }

        val settings = runBlocking { settingsRepository.settings.first() }
        val hasVibrator = vibrator.hasVibrator()
        hapticEnabled.set(settings.hapticFeedbackEnabled && hasVibrator)
        hapticStrength.set(settings.hapticStrength)
        Log.d(TAG, "Haptic config: enabled=${settings.hapticFeedbackEnabled}, hasVibrator=$hasVibrator, strength=${settings.hapticStrength}")

        isPlaying.set(true)

        playbackThread = Thread({
            runPlaybackLoop(samples)
        }, "MetronomePlayback").apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    fun stop() {
        isPlaying.set(false)
        playbackThread?.join(THREAD_JOIN_TIMEOUT_MS)
        playbackThread = null
    }

    fun updateBpm(bpm: Int) {
        currentBpm.set(bpm)
    }

    fun release() {
        stop()
        clickSamples = null
    }

    private fun runPlaybackLoop(clickSamples: ShortArray) {
        val bufferSizeInBytes = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSizeInBytes)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()

        // Calculate audio latency: buffer size in samples / sample rate * 1000 for ms
        // Buffer is in bytes, 2 bytes per sample (16-bit PCM)
        // Add extra offset for DAC/driver latency that isn't captured by buffer size
        val bufferSizeInSamples = bufferSizeInBytes / 2
        val bufferLatencyMs = (bufferSizeInSamples * 1000L) / SAMPLE_RATE
        val audioLatencyMs = bufferLatencyMs + ADDITIONAL_AUDIO_LATENCY_MS
        Log.d(TAG, "Audio buffer: $bufferSizeInBytes bytes, $bufferSizeInSamples samples, buffer latency ~${bufferLatencyMs}ms, total latency ~${audioLatencyMs}ms")

        // Handler for scheduling vibrations
        val vibrationThread = HandlerThread("VibrationThread").apply { start() }
        val vibrationHandler = Handler(vibrationThread.looper)

        audioTrack.play()

        try {
            while (isPlaying.get()) {
                val bpm = currentBpm.get()

                // Calculate samples per beat: at 44100 Hz, 60 BPM = 44100 samples per beat
                val samplesPerBeat = SAMPLE_RATE * 60 / bpm
                val silenceSamples = samplesPerBeat - clickSamples.size

                // Schedule haptic feedback to fire after audio latency
                if (hapticEnabled.get()) {
                    vibrationHandler.postDelayed({
                        triggerVibration()
                    }, audioLatencyMs)
                }

                // Write click sound
                audioTrack.write(clickSamples, 0, clickSamples.size)

                // Write silence for the remainder of the beat
                if (silenceSamples > 0 && isPlaying.get()) {
                    val silence = ShortArray(minOf(silenceSamples, SILENCE_CHUNK_SIZE))
                    var remaining = silenceSamples

                    while (remaining > 0 && isPlaying.get()) {
                        val toWrite = minOf(remaining, silence.size)
                        audioTrack.write(silence, 0, toWrite)
                        remaining -= toWrite
                    }
                }
            }
        } finally {
            audioTrack.stop()
            audioTrack.release()
            vibrationHandler.removeCallbacksAndMessages(null)
            vibrationThread.quitSafely()
        }
    }

    private fun triggerVibration() {
        try {
            val strength = hapticStrength.get()
            val hasAmplitude = vibrator.hasAmplitudeControl()
            val amplitude = if (hasAmplitude) strength.amplitude else VibrationEffect.DEFAULT_AMPLITUDE

            val effect = VibrationEffect.createOneShot(strength.durationMs, amplitude)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val attrs = android.os.VibrationAttributes.Builder()
                    .setUsage(android.os.VibrationAttributes.USAGE_ALARM)
                    .build()
                vibrator.vibrate(effect, attrs)
            } else {
                vibrator.vibrate(effect)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to vibrate", e)
        }
    }

    companion object {
        private const val TAG = "MetronomePlayer"
        private const val SAMPLE_RATE = 44100
        private const val WAV_HEADER_SIZE = 44
        private const val THREAD_JOIN_TIMEOUT_MS = 1000L
        private const val SILENCE_CHUNK_SIZE = 4410 // ~100ms chunks for responsive stopping
        private const val ADDITIONAL_AUDIO_LATENCY_MS = 45L // Extra latency for DAC/driver
    }
}
