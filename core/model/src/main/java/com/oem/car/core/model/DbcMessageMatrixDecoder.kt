package com.oem.car.core.model

/**
 * ============================================================================
 * DBC (Database CAN) 消息信号矩阵编解码器 (DBC Message Matrix Pipeline)
 * ============================================================================
 *
 * 统一解码车载 CAN 矩阵报文。支持多 CAN ID 的解析与信号解包：
 * - 0x120: 车辆动态 (车速、档位、电源、Rolling Counter、CRC8)
 * - 0x210: 空调状态 (主驾温度、副驾温度、风速、AC开关)
 * - 0x300: 电池能源 (SOC百分比、电压、电流、续航里程)
 * - 0x400: ADAS智驾 (ACC状态、AEB预警、车道偏离、目标距离)
 */
sealed interface DbcDecodedMessage {
    val canId: Int
    val timestampMs: Long

    /** CAN 0x120 - 车辆动态信号 */
    data class VehicleDynamic(
        override val canId: Int = CAN_ID_VEHICLE_DYNAMIC,
        override val timestampMs: Long = System.currentTimeMillis(),
        val speedKmh: Float,
        val gear: VehicleGear,
        val powerState: PowerState,
        val counter: Int,
        val isCrcValid: Boolean
    ) : DbcDecodedMessage

    /** CAN 0x210 - 空调系统状态信号 */
    data class ClimateStatus(
        override val canId: Int = CAN_ID_CLIMATE_STATUS,
        override val timestampMs: Long = System.currentTimeMillis(),
        val driverTempCelsius: Float,
        val passengerTempCelsius: Float,
        val fanSpeedLevel: Int, // 0..7
        val isAcOn: Boolean
    ) : DbcDecodedMessage

    /** CAN 0x300 - 动力电池与能耗信号 */
    data class BatteryEnergy(
        override val canId: Int = CAN_ID_BATTERY_ENERGY,
        override val timestampMs: Long = System.currentTimeMillis(),
        val batterySocPercent: Float, // 0.0 .. 100.0 %
        val batteryVoltageVolts: Float,
        val batteryCurrentAmps: Float,
        val remainingRangeKm: Int
    ) : DbcDecodedMessage

    /** CAN 0x400 - ADAS 智驾与感知状态信号 */
    data class AdasStatus(
        override val canId: Int = CAN_ID_ADAS_STATUS,
        override val timestampMs: Long = System.currentTimeMillis(),
        val isAccActive: Boolean,
        val isAebWarning: Boolean,
        val isLaneDepartureWarning: Boolean,
        val targetDistanceMeters: Float
    ) : DbcDecodedMessage

    companion object {
        const val CAN_ID_VEHICLE_DYNAMIC = 0x120
        const val CAN_ID_CLIMATE_STATUS = 0x210
        const val CAN_ID_BATTERY_ENERGY = 0x300
        const val CAN_ID_ADAS_STATUS = 0x400
    }
}

object DbcMessageMatrixDecoder {

    /**
     * 将泛化的 [CanFrame] 转化为结构化的 [DbcDecodedMessage]。
     * 如果 CAN ID 未在矩阵中定义或载荷长度不足，则返回 null。
     */
    fun decodeFrame(frame: CanFrame): DbcDecodedMessage? {
        if (frame.payload.size < 8) return null

        return when (frame.id) {
            DbcDecodedMessage.CAN_ID_VEHICLE_DYNAMIC -> decodeVehicleDynamic(frame)
            DbcDecodedMessage.CAN_ID_CLIMATE_STATUS -> decodeClimateStatus(frame)
            DbcDecodedMessage.CAN_ID_BATTERY_ENERGY -> decodeBatteryEnergy(frame)
            DbcDecodedMessage.CAN_ID_ADAS_STATUS -> decodeAdasStatus(frame)
            else -> null
        }
    }

    /**
     * 编码 CAN 0x120 车辆动态报文。
     */
    fun encodeVehicleDynamic(
        speedKmh: Float,
        gear: VehicleGear,
        powerState: PowerState,
        counter: Int
    ): CanFrame {
        val gearCode = when (gear) {
            VehicleGear.PARK -> 1
            VehicleGear.REVERSE -> 2
            VehicleGear.NEUTRAL -> 4
            VehicleGear.DRIVE -> 8
        }
        val powerCode = when (powerState) {
            PowerState.RUNNING -> 0
            PowerState.STR_SLEEP -> 1
            PowerState.GARAGE_MODE -> 2
            PowerState.SHUTDOWN_PREPARE -> 3
        }
        return CanMessageCodec.encodeVehicleSignal(
            speed = speedKmh,
            gear = gearCode,
            powerState = powerCode,
            counter = counter
        )
    }

    private fun decodeVehicleDynamic(frame: CanFrame): DbcDecodedMessage.VehicleDynamic? {
        val decoded = CanMessageCodec.decodeVehicleSignal(frame) ?: return null
        val gear = when (decoded.gear) {
            1 -> VehicleGear.PARK
            2 -> VehicleGear.REVERSE
            4 -> VehicleGear.NEUTRAL
            8 -> VehicleGear.DRIVE
            else -> VehicleGear.PARK
        }
        val power = when (decoded.powerState) {
            0 -> PowerState.RUNNING
            1 -> PowerState.STR_SLEEP
            2 -> PowerState.GARAGE_MODE
            3 -> PowerState.SHUTDOWN_PREPARE
            else -> PowerState.RUNNING
        }

        return DbcDecodedMessage.VehicleDynamic(
            canId = frame.id,
            timestampMs = frame.timestampMs,
            speedKmh = decoded.speed,
            gear = gear,
            powerState = power,
            counter = decoded.counter,
            isCrcValid = decoded.isCrcValid
        )
    }

    /**
     * 编码 CAN 0x210 空调系统状态报文。
     * Byte 0: 主驾温度 (Temp * 2, e.g. 24.5C -> 49)
     * Byte 1: 副驾温度 (Temp * 2)
     * Byte 2: 风速等级 (0..7)
     * Byte 3: AC开关 (0x01 = ON, 0x00 = OFF)
     */
    fun encodeClimateStatus(
        driverTempCelsius: Float,
        passengerTempCelsius: Float,
        fanSpeedLevel: Int,
        isAcOn: Boolean
    ): CanFrame {
        val payload = ByteArray(8)
        payload[0] = (driverTempCelsius * 2).toInt().coerceIn(32, 64).toByte() // 16.0C ~ 32.0C
        payload[1] = (passengerTempCelsius * 2).toInt().coerceIn(32, 64).toByte()
        payload[2] = fanSpeedLevel.coerceIn(0, 7).toByte()
        payload[3] = if (isAcOn) 0x01.toByte() else 0x00.toByte()
        payload[7] = CanMessageCodec.calculateCrc8(payload, 7)

        return CanFrame(
            id = DbcDecodedMessage.CAN_ID_CLIMATE_STATUS,
            dlc = 8,
            payload = payload
        )
    }

    private fun decodeClimateStatus(frame: CanFrame): DbcDecodedMessage.ClimateStatus {
        val p = frame.payload
        val driverTemp = (p[0].toInt() and 0xFF) / 2.0f
        val passengerTemp = (p[1].toInt() and 0xFF) / 2.0f
        val fanSpeed = p[2].toInt() and 0x07
        val isAcOn = p[3].toInt() == 0x01

        return DbcDecodedMessage.ClimateStatus(
            canId = frame.id,
            timestampMs = frame.timestampMs,
            driverTempCelsius = driverTemp,
            passengerTempCelsius = passengerTemp,
            fanSpeedLevel = fanSpeed,
            isAcOn = isAcOn
        )
    }

    /**
     * 编码 CAN 0x300 动力电池与能耗报文。
     * Byte 0~1: SOC * 10 (e.g. 85.5% -> 855, 大端序)
     * Byte 2~3: Voltage * 10 (e.g. 400.0V -> 4000)
     * Byte 4~5: Current * 10 + 5000 (支持正负电流，偏置 500A)
     * Byte 6~7: 剩余续航 (km, 16-bit 大端序)
     */
    fun encodeBatteryEnergy(
        batterySocPercent: Float,
        batteryVoltageVolts: Float,
        batteryCurrentAmps: Float,
        remainingRangeKm: Int
    ): CanFrame {
        val payload = ByteArray(8)
        val socRaw = (batterySocPercent * 10).toInt().coerceIn(0, 1000)
        val voltageRaw = (batteryVoltageVolts * 10).toInt().coerceIn(0, 10000)
        val currentRaw = ((batteryCurrentAmps + 500f) * 10).toInt().coerceIn(0, 10000)
        val rangeRaw = remainingRangeKm.coerceIn(0, 2000)

        payload[0] = ((socRaw shr 8) and 0xFF).toByte()
        payload[1] = (socRaw and 0xFF).toByte()

        payload[2] = ((voltageRaw shr 8) and 0xFF).toByte()
        payload[3] = (voltageRaw and 0xFF).toByte()

        payload[4] = ((currentRaw shr 8) and 0xFF).toByte()
        payload[5] = (currentRaw and 0xFF).toByte()

        payload[6] = ((rangeRaw shr 8) and 0xFF).toByte()
        payload[7] = (rangeRaw and 0xFF).toByte()

        return CanFrame(
            id = DbcDecodedMessage.CAN_ID_BATTERY_ENERGY,
            dlc = 8,
            payload = payload
        )
    }

    private fun decodeBatteryEnergy(frame: CanFrame): DbcDecodedMessage.BatteryEnergy {
        val p = frame.payload
        val socRaw = ((p[0].toInt() and 0xFF) shl 8) or (p[1].toInt() and 0xFF)
        val voltageRaw = ((p[2].toInt() and 0xFF) shl 8) or (p[3].toInt() and 0xFF)
        val currentRaw = ((p[4].toInt() and 0xFF) shl 8) or (p[5].toInt() and 0xFF)
        val rangeRaw = ((p[6].toInt() and 0xFF) shl 8) or (p[7].toInt() and 0xFF)

        return DbcDecodedMessage.BatteryEnergy(
            canId = frame.id,
            timestampMs = frame.timestampMs,
            batterySocPercent = socRaw / 10.0f,
            batteryVoltageVolts = voltageRaw / 10.0f,
            batteryCurrentAmps = (currentRaw / 10.0f) - 500f,
            remainingRangeKm = rangeRaw
        )
    }

    /**
     * 编码 CAN 0x400 ADAS 智驾与感知状态报文。
     * Byte 0: 标志位 (bit0: ACC active, bit1: AEB warning, bit2: Lane departure warning)
     * Byte 1~2: 目标车距 meters * 10 (e.g. 25.5m -> 255)
     */
    fun encodeAdasStatus(
        isAccActive: Boolean,
        isAebWarning: Boolean,
        isLaneDepartureWarning: Boolean,
        targetDistanceMeters: Float
    ): CanFrame {
        val payload = ByteArray(8)
        var flags = 0
        if (isAccActive) flags = flags or 0x01
        if (isAebWarning) flags = flags or 0x02
        if (isLaneDepartureWarning) flags = flags or 0x04
        payload[0] = flags.toByte()

        val distRaw = (targetDistanceMeters * 10).toInt().coerceIn(0, 2500)
        payload[1] = ((distRaw shr 8) and 0xFF).toByte()
        payload[2] = (distRaw and 0xFF).toByte()

        payload[7] = CanMessageCodec.calculateCrc8(payload, 7)

        return CanFrame(
            id = DbcDecodedMessage.CAN_ID_ADAS_STATUS,
            dlc = 8,
            payload = payload
        )
    }

    private fun decodeAdasStatus(frame: CanFrame): DbcDecodedMessage.AdasStatus {
        val p = frame.payload
        val flags = p[0].toInt() and 0xFF
        val isAccActive = (flags and 0x01) != 0
        val isAebWarning = (flags and 0x02) != 0
        val isLaneDeparture = (flags and 0x04) != 0

        val distRaw = ((p[1].toInt() and 0xFF) shl 8) or (p[2].toInt() and 0xFF)

        return DbcDecodedMessage.AdasStatus(
            canId = frame.id,
            timestampMs = frame.timestampMs,
            isAccActive = isAccActive,
            isAebWarning = isAebWarning,
            isLaneDepartureWarning = isLaneDeparture,
            targetDistanceMeters = distRaw / 10.0f
        )
    }
}
