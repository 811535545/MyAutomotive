package com.oem.car.core.audio

import com.oem.car.core.common.CarLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ============================================================================
 * 车规级音量平滑渐变与 Fade-In / Fade-Out / Ducking 调度器
 * ============================================================================
 *
 * 避免音量瞬变产生的 Pop 爆音与听觉突触感。
 */
class AudioVolumeFader(
    private val scope: CoroutineScope,
    initialVolume: Float = 1.0f
) {
    companion object {
        private const val TAG = "AudioVolumeFader"
        private const val FADE_STEP_MS = 20L // 20ms 刷新步长
    }

    private val _currentVolume = MutableStateFlow(initialVolume)
    val currentVolume: StateFlow<Float> = _currentVolume.asStateFlow()

    private var fadeJob: Job? = null

    /**
     * 向目标音量执行平滑渐变 (Volume Ramp Smoothing)。
     *
     * @param targetVolume 目标音量增益 (0.0f .. 1.0f)
     * @param durationMs 渐变持续时间 (毫秒，如 200ms ~ 500ms)
     */
    fun animateToVolume(targetVolume: Float, durationMs: Long = 300L) {
        val clampedTarget = targetVolume.coerceIn(0.0f, 1.0f)
        val startVolume = _currentVolume.value

        if (Math.abs(startVolume - clampedTarget) < 0.01f) {
            _currentVolume.value = clampedTarget
            return
        }

        fadeJob?.cancel()
        fadeJob = scope.launch(Dispatchers.Default) {
            val steps = (durationMs / FADE_STEP_MS).toInt().coerceAtLeast(1)
            val deltaPerStep = (clampedTarget - startVolume) / steps

            CarLogger.d(TAG, "Starting volume fade: $startVolume -> $clampedTarget over ${durationMs}ms ($steps steps)")

            for (i in 1..steps) {
                delay(FADE_STEP_MS)
                val newVol = (startVolume + deltaPerStep * i).coerceIn(0.0f, 1.0f)
                _currentVolume.value = newVol
            }
            _currentVolume.value = clampedTarget
            CarLogger.d(TAG, "Volume fade completed: $clampedTarget")
        }
    }
}
