package com.ironcore.metrics.ui.workout

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper that simulates syncing music tempo to heart rate.
 */
@Singleton
class MusicSyncHelper @Inject constructor() {
    companion object {
        private const val TAG = "MusicSyncHelper"
    }

    fun updateTempoToHeartRate(bpm: Long) {
        val targetTempo = when {
            bpm > 160 -> "Fast (180 BPM)"
            bpm > 120 -> "Moderate (140 BPM)"
            else -> "Chilled (100 BPM)"
        }
        Log.d(TAG, "Updating playback tempo to $targetTempo based on HR: $bpm")
    }
}
