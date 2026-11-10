package com.oem.car.core.audio

/**
 * ============================================================================
 * AAOS 标准车载音频总线与音区配置模型 (Car Audio Bus & Zone Specification)
 * ============================================================================
 *
 * 符合 Android Automotive OS (`packages/services/Car/service/res/xml/car_audio_configuration.xml`) 规范。
 * 定义物理设备总线 (Audio Bus)、音区 (Audio Zone) 与音频用途 (CarAudioUsage) 的绑定映射关系。
 */

/**
 * 车规音频用途枚举 (映射 Android AudioAttributes.USAGE_*)
 */
enum class CarAudioUsage(val priority: Int, val description: String) {
    /** 紧急报警/ADAS警告 (最高优先级) */
    EMERGENCY_ALARM(priority = 100, description = "紧急告警/ADAS防碰撞"),

    /** 电话呼叫/车载蓝牙通话 */
    CALL(priority = 90, description = "蓝牙电话/紧急呼叫"),

    /** PTT 语音助手交互 */
    VOICE_COMMAND(priority = 80, description = "语音助手/方向盘PTT"),

    /** 导航路口播报/逐段指引 */
    NAVIGATION(priority = 70, description = "导航语音播报"),

    /** 系统提示音/倒车雷达滴滴音 */
    SYSTEM_SOUND(priority = 60, description = "系统提示音/雷达警报"),

    /** 多媒体音乐/电台/视频 (常规背景音) */
    MEDIA(priority = 10, description = "多媒体音乐/广播/视频音轨")
}

/**
 * 车规音频物理 Bus 设备抽象。
 *
 * 在 AAOS 中，不同的 AudioAttributes 会路由到不同的硬件 Audio Bus 设备端口。
 *
 * @property busName 物理总线标识符 (如 `bus0_media_driver`, `bus1_nav_driver`)
 * @property address HAL/ALSA 音频设备物理地址
 * @property supportedUsages 该 Bus 绑定的用法集合
 * @property currentVolumeGainDb 当前物理音益增益 (dB)
 * @property isMuted 是否物理静音
 */
data class AudioBusDevice(
    val busName: String,
    val address: String,
    val supportedUsages: Set<CarAudioUsage>,
    val currentVolumeGainDb: Float = 0.0f,
    val isMuted: Boolean = false
)

/**
 * 物理音区配置。
 */
data class CarAudioZoneConfig(
    val zoneId: Int,
    val zoneName: String,
    val isDefaultZone: Boolean = false,
    val busDevices: List<AudioBusDevice>
)

/**
 * AAOS 标准音区预设配置工厂 (AAOS Audio Configuration Factory)。
 */
object CarAudioConfigPresets {

    /** 主驾音区 (Driver Zone 0) */
    val DRIVER_ZONE_0 = CarAudioZoneConfig(
        zoneId = 0,
        zoneName = "主驾音区 (Driver Zone 0)",
        isDefaultZone = true,
        busDevices = listOf(
            AudioBusDevice(
                busName = "bus0_media_driver",
                address = "bus0_media_driver_out",
                supportedUsages = setOf(CarAudioUsage.MEDIA)
            ),
            AudioBusDevice(
                busName = "bus1_nav_driver",
                address = "bus1_nav_driver_out",
                supportedUsages = setOf(CarAudioUsage.NAVIGATION, CarAudioUsage.SYSTEM_SOUND)
            ),
            AudioBusDevice(
                busName = "bus2_call_headrest",
                address = "bus2_headrest_speaker_out",
                supportedUsages = setOf(CarAudioUsage.CALL, CarAudioUsage.VOICE_COMMAND, CarAudioUsage.EMERGENCY_ALARM)
            )
        )
    )

    /** 副驾音区 (Passenger Zone 1) */
    val PASSENGER_ZONE_1 = CarAudioZoneConfig(
        zoneId = 1,
        zoneName = "副驾音区 (Passenger Zone 1)",
        isDefaultZone = false,
        busDevices = listOf(
            AudioBusDevice(
                busName = "bus3_media_passenger",
                address = "bus3_passenger_speaker_out",
                supportedUsages = setOf(CarAudioUsage.MEDIA, CarAudioUsage.SYSTEM_SOUND)
            )
        )
    )

    /** 后排音区 (Rear Zone 2) */
    val REAR_ZONE_2 = CarAudioZoneConfig(
        zoneId = 2,
        zoneName = "后排音区 (Rear Zone 2)",
        isDefaultZone = false,
        busDevices = listOf(
            AudioBusDevice(
                busName = "bus4_rear_entertainment",
                address = "bus4_rear_headset_out",
                supportedUsages = setOf(CarAudioUsage.MEDIA, CarAudioUsage.SYSTEM_SOUND)
            )
        )
    )

    /** 全车音区预设映射表 */
    val DEFAULT_ZONE_CONFIGS: Map<Int, CarAudioZoneConfig> = mapOf(
        0 to DRIVER_ZONE_0,
        1 to PASSENGER_ZONE_1,
        2 to REAR_ZONE_2
    )
}
