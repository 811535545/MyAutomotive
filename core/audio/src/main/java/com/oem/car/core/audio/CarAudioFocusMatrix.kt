package com.oem.car.core.audio

import com.oem.car.core.common.CarLogger

/**
 * Focus 焦点仲裁结果类型 (符合 AAOS AudioFocus 规范)。
 */
sealed interface AudioFocusDecision {
    /** 允许播放且无需调整其他音源音量 */
    object Granted : AudioFocusDecision

    /** 允许播放，但要求同音区/全车当前媒体降低音量 (Ducking 平滑压音) */
    data class GrantedDuck(val targetVolumeMultiplier: Float = 0.3f) : AudioFocusDecision

    /** 允许播放，但要求同音区当前低优先级音源暂停/打断 (Exclusive Pause) */
    object GrantedExclusive : AudioFocusDecision

    /** 拒绝焦点申请 (例如在紧急呼叫/ADAS报警生效时，拒绝普通音效) */
    data class Rejected(val reason: String) : AudioFocusDecision
}

/**
 * ============================================================================
 * AAOS 车规级 AudioFocus 交互仲裁矩阵 (Car Audio Focus Interaction Matrix)
 * ============================================================================
 *
 * 在 AAOS 标准中，音频焦点矩阵定义了当一个“新音源 (Incoming Usage)”向特定音区申请焦点时，
 * 与该音区“现有音源 (Active Usage)”之间的交互规则 (Interference Matrix)。
 */
object CarAudioFocusMatrix {

    private const val TAG = "CarAudioFocusMatrix"

    /**
     * 计算焦点仲裁决策。
     *
     * @param requestZone 申请焦点的音区
     * @param incomingUsage 申请的音频用途
     * @param activeUsages In-flight 正在播放的音频用途集合
     * @return [AudioFocusDecision] 仲裁结果
     */
    fun evaluateFocusRequest(
        requestZone: AudioZone,
        incomingUsage: CarAudioUsage,
        activeUsages: Set<CarAudioUsage>
    ): AudioFocusDecision {
        // 规则 1: 紧急告警 (EMERGENCY_ALARM) 强制独占打断一切音源
        if (incomingUsage == CarAudioUsage.EMERGENCY_ALARM) {
            CarLogger.w(TAG, "[$requestZone] EMERGENCY_ALARM incoming! Forcing Exclusive Focus Override.")
            return AudioFocusDecision.GrantedExclusive
        }

        // 规则 2: 当正处于紧急告警状态时，拒绝低优先级的媒体与系统音申请
        if (activeUsages.contains(CarAudioUsage.EMERGENCY_ALARM) && incomingUsage.priority < CarAudioUsage.EMERGENCY_ALARM.priority) {
            CarLogger.w(TAG, "[$requestZone] Active EMERGENCY_ALARM blocking incoming request: $incomingUsage")
            return AudioFocusDecision.Rejected("Blocked by active EMERGENCY_ALARM")
        }

        // 规则 3: 蓝牙电话 / 紧急呼叫 (CALL) 优先级高于常规媒体与导航
        if (incomingUsage == CarAudioUsage.CALL) {
            CarLogger.i(TAG, "[$requestZone] CALL incoming! Granting Exclusive focus for privacy channel.")
            return AudioFocusDecision.GrantedExclusive
        }

        // 规则 4: PTT 语音助手 (VOICE_COMMAND) 触发时，压低全车媒体音量至 10%
        if (incomingUsage == CarAudioUsage.VOICE_COMMAND) {
            CarLogger.i(TAG, "[$requestZone] VOICE_COMMAND (PTT) active! Granting Ducking (0.1x).")
            return AudioFocusDecision.GrantedDuck(targetVolumeMultiplier = 0.1f)
        }

        // 规则 5: 导航播报 (NAVIGATION)
        if (incomingUsage == CarAudioUsage.NAVIGATION) {
            return if (requestZone == AudioZone.DRIVER_ZONE_0) {
                // 主驾音区导航播报时：主驾媒体降低音量至 30%
                CarLogger.i(TAG, "[Driver Zone 0] NAVIGATION active. Ducking driver media to 0.3x.")
                AudioFocusDecision.GrantedDuck(targetVolumeMultiplier = 0.3f)
            } else {
                // 副驾/后排音区不受主驾导航影响
                CarLogger.i(TAG, "[$requestZone] Non-driver zone NAVIGATION. Granting standard focus.")
                AudioFocusDecision.Granted
            }
        }

        // 规则 6: 常规多媒体 (MEDIA) 申请
        if (incomingUsage == CarAudioUsage.MEDIA) {
            if (activeUsages.contains(CarAudioUsage.VOICE_COMMAND)) {
                return AudioFocusDecision.GrantedDuck(targetVolumeMultiplier = 0.1f)
            }
            if (activeUsages.contains(CarAudioUsage.NAVIGATION) && requestZone == AudioZone.DRIVER_ZONE_0) {
                return AudioFocusDecision.GrantedDuck(targetVolumeMultiplier = 0.3f)
            }
        }

        return AudioFocusDecision.Granted
    }
}
