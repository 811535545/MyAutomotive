package com.oem.car.core.audio

import com.oem.car.core.common.CarLogger
import com.oem.car.core.model.AudioZoneState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ============================================================================
 * 车载音频管理器统一外观代理 (Car Audio Manager Delegate)
 * ============================================================================
 *
 * 连接上层 Repository/ViewModel 与底层 [MultiZoneAudioManager] 仲裁引擎。
 */
class CarAudioManagerDelegate(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    val multiZoneManager = MultiZoneAudioManager()

    /** 音量平滑渐变调度器 */
    val volumeFader = AudioVolumeFader(scope, initialVolume = 1.0f)

    private val _audioZoneState = MutableStateFlow(AudioZoneState())
    val audioZoneState: StateFlow<AudioZoneState> = _audioZoneState.asStateFlow()

    /**
     * 触发方向盘 PTT 语音对讲唤醒。
     */
    fun triggerPttEvent() {
        CarLogger.i(TAG, "Steering wheel PTT pressed! Requesting VOICE_COMMAND audio focus & ducking music...")
        val decision = multiZoneManager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.VOICE_COMMAND)
        if (decision !is AudioFocusDecision.Rejected) {
            volumeFader.animateToVolume(0.1f, durationMs = 250L)
            _audioZoneState.value = AudioZoneState(
                isPttActive = true,
                lastPttTimestamp = System.currentTimeMillis(),
                audioFocusDuck = true
            )
        }
    }

    /**
     * 结束 PTT 语音会话。
     */
    fun releasePttEvent() {
        CarLogger.i(TAG, "PTT session finished. Restoring background audio focus...")
        multiZoneManager.abandonAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.VOICE_COMMAND)
        volumeFader.animateToVolume(1.0f, durationMs = 300L)
        _audioZoneState.value = _audioZoneState.value.copy(
            isPttActive = false,
            audioFocusDuck = false
        )
    }

    /**
     * 触发主驾导航语音播报。
     */
    fun triggerNavGuidanceStart() {
        CarLogger.i(TAG, "Driver Navigation guidance started. Requesting NAVIGATION focus...")
        multiZoneManager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.NAVIGATION)
        volumeFader.animateToVolume(0.3f, durationMs = 200L)
    }

    /**
     * 结束主驾导航语音播报。
     */
    fun triggerNavGuidanceEnd() {
        CarLogger.i(TAG, "Driver Navigation guidance ended. Abandoning NAVIGATION focus...")
        multiZoneManager.abandonAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.NAVIGATION)
        volumeFader.animateToVolume(1.0f, durationMs = 300L)
    }

    /**
     * 触发 ADAS 紧急碰撞警报 (最高优先级硬打断)。
     */
    fun triggerEmergencyAlarm() {
        CarLogger.w(TAG, "EMERGENCY ALARM TRIGGERED! Overriding all audio zones...")
        multiZoneManager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.EMERGENCY_ALARM)
        volumeFader.animateToVolume(0.0f, durationMs = 100L)
    }

    /**
     * 清除紧急警报。
     */
    fun clearEmergencyAlarm() {
        CarLogger.i(TAG, "Emergency alarm cleared. Restoring normal audio zones...")
        multiZoneManager.abandonAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.EMERGENCY_ALARM)
        volumeFader.animateToVolume(1.0f, durationMs = 300L)
    }

    companion object {
        private const val TAG = "CarAudioDelegate"
    }
}
