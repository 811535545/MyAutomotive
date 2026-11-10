package com.oem.car.core.audio

import com.oem.car.core.common.CarLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap

/**
 * 车载多音区定义 (符合 AAOS CarAudioService 多音区标准)。
 */
enum class AudioZone(val zoneId: Int, val description: String) {
    DRIVER_ZONE_0(0, "主驾音区 (Driver Zone 0)"),
    PASSENGER_ZONE_1(1, "副驾音区 (Passenger Zone 1)"),
    REAR_ZONE_2(2, "后排音区 (Rear Zone 2)")
}

/**
 * 各音区状态快照。
 */
data class ZoneAudioStatus(
    val zone: AudioZone,
    val isMediaPlaying: Boolean = false,
    val isNavGuiding: Boolean = false,
    val isVoiceActive: Boolean = false,
    val isCallActive: Boolean = false,
    val isEmergencyActive: Boolean = false,
    val volumeMultiplier: Float = 1.0f,
    val activeUsages: Set<CarAudioUsage> = emptySet(),
    val activeBusDevices: List<AudioBusDevice> = emptyList()
)

/**
 * ============================================================================
 * 商业级多音区音频焦点与总线仲裁引擎 (Multi-Zone Audio Focus & Bus Arbitrator)
 * ============================================================================
 *
 * 核心功能：
 * 1. 结合 [CarAudioFocusMatrix] 仲裁规则引擎，精准判定 Focus 申请与打断策略；
 * 2. 依照 [CarAudioConfigPresets] 将 AudioUsage 绑定路由至物理 Audio Bus 端口；
 * 3. 实现主驾音区 (Zone 0) 与副驾/后排 (Zone 1/2) 物理隔离的 Ducking 混音；
 * 4. 支持紧急告警 (EMERGENCY_ALARM) 与蓝牙呼叫 (CALL) 优先级抢占。
 */
class MultiZoneAudioManager {

    companion object {
        private const val TAG = "MultiZoneAudioManager"
    }

    /** 音区与配置预设字典 */
    private val zoneConfigs = CarAudioConfigPresets.DEFAULT_ZONE_CONFIGS

    /** 追踪每个音区当前处于 In-Flight 播放状态的 AudioUsage 集合 */
    private val zoneActiveUsages = ConcurrentHashMap<AudioZone, MutableSet<CarAudioUsage>>().apply {
        AudioZone.entries.forEach { this[it] = ConcurrentHashMap.newKeySet() }
    }

    /** 暴露给 UI 与仓库层的多音区响应式状态 Map */
    private val _zoneStatuses = MutableStateFlow(
        mapOf(
            AudioZone.DRIVER_ZONE_0 to ZoneAudioStatus(AudioZone.DRIVER_ZONE_0),
            AudioZone.PASSENGER_ZONE_1 to ZoneAudioStatus(AudioZone.PASSENGER_ZONE_1),
            AudioZone.REAR_ZONE_2 to ZoneAudioStatus(AudioZone.REAR_ZONE_2)
        )
    )

    val zoneStatuses: StateFlow<Map<AudioZone, ZoneAudioStatus>> = _zoneStatuses.asStateFlow()

    /**
     * 申请指定音区的音频焦点。
     *
     * @param zone 目标音区
     * @param usage 申请的音频用途
     * @return [AudioFocusDecision] 焦点仲裁决议
     */
    fun requestAudioFocus(zone: AudioZone, usage: CarAudioUsage): AudioFocusDecision {

        val activeSet = zoneActiveUsages[zone] ?: ConcurrentHashMap.newKeySet()
        
        // 调用 Focus 仲裁矩阵判定
        val decision = CarAudioFocusMatrix.evaluateFocusRequest(zone, usage, activeSet)
        CarLogger.i(TAG, "AudioFocus requested: zone=${zone.name}, usage=${usage.name} -> Decision=$decision")

        when (decision) {
            is AudioFocusDecision.Rejected -> {
                CarLogger.w(TAG, "AudioFocus rejected for zone ${zone.name}, usage=${usage.name}: ${decision.reason}")
                return decision
            }
            else -> {
                activeSet.add(usage)
                recalculateZoneStates()
                return decision
            }
        }

    }

    /**
     * 释放指定音区的音频焦点。
     *
     * @param zone 目标音区
     * @param usage 挂起的音频用途
     */
    fun abandonAudioFocus(zone: AudioZone, usage: CarAudioUsage) {
        val activeSet = zoneActiveUsages[zone] ?: return
        if (activeSet.remove(usage)) {
            CarLogger.i(TAG, "AudioFocus abandoned: zone=${zone.name}, usage=${usage.name}")
            recalculateZoneStates()
        }
    }

    /**
     * 查询指定音区内对应 CarAudioUsage 所路由到的物理 AudioBus 端口。
     */
    fun getAudioBusForUsage(zone: AudioZone, usage: CarAudioUsage): AudioBusDevice? {
        val config = zoneConfigs[zone.zoneId] ?: return null
        return config.busDevices.firstOrNull { it.supportedUsages.contains(usage) }
    }

    /**
     * 重新计算所有音区的音量增益与焦点快照。
     */
    private fun recalculateZoneStates() {
        val newMap = _zoneStatuses.value.toMutableMap()
        val allActiveUsages = zoneActiveUsages.values.flatten().toSet()
        val isGlobalVoiceActive = allActiveUsages.contains(CarAudioUsage.VOICE_COMMAND)
        val isGlobalEmergencyActive = allActiveUsages.contains(CarAudioUsage.EMERGENCY_ALARM)

        AudioZone.entries.forEach { zone ->
            val usages = zoneActiveUsages[zone]?.toSet() ?: emptySet()
            val isNavActive = usages.contains(CarAudioUsage.NAVIGATION)

            // 计算该音区的目标音量系数 (Multiplier)
            val newMultiplier = when {
                isGlobalEmergencyActive -> 0.0f // 紧急告警触发时，非告警音源静音
                isGlobalVoiceActive -> 0.1f // 全车 PTT 语音时，媒体/导航深度压音至 10%
                zone == AudioZone.DRIVER_ZONE_0 && isNavActive -> 0.3f // 主驾导航播报时，主驾媒体压音至 30%
                else -> 1.0f // 副驾与后排不受主驾导航影响 (100%)
            }

            val zoneConfig = zoneConfigs[zone.zoneId]
            val activeBuses = zoneConfig?.busDevices?.filter { bus ->
                bus.supportedUsages.any { usages.contains(it) }
            } ?: emptyList()

            newMap[zone] = ZoneAudioStatus(
                zone = zone,
                isMediaPlaying = usages.contains(CarAudioUsage.MEDIA),
                isNavGuiding = isNavActive,
                isVoiceActive = usages.contains(CarAudioUsage.VOICE_COMMAND),
                isCallActive = usages.contains(CarAudioUsage.CALL),
                isEmergencyActive = usages.contains(CarAudioUsage.EMERGENCY_ALARM),
                volumeMultiplier = newMultiplier,
                activeUsages = usages,
                activeBusDevices = activeBuses
            )
        }

        _zoneStatuses.value = newMap
    }
}
