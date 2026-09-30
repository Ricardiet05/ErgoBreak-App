package com.example.audio

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log

class SoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
        } catch (e: Exception) {
            Log.w("SoundManager", "ToneGenerator init failed, proceeding silently", e)
        }
    }

    fun playCountdownTick() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (e: Exception) {
            // Ignore sound error
        }
    }

    fun playCountdownGo() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 220)
        } catch (e: Exception) {
            // Ignore sound error
        }
    }

    fun playTimerTick() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 50)
        } catch (e: Exception) {
            // Ignore sound error
        }
    }

    fun playExerciseComplete() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 250)
        } catch (e: Exception) {
            // Ignore sound error
        }
    }

    fun playBreakDone() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 300)
        } catch (e: Exception) {
            // Ignore sound error
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // Ignore
        }
    }
}
