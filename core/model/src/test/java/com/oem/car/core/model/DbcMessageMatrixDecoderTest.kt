package com.oem.car.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DbcMessageMatrixDecoderTest {

    @Test
    fun testVehicleDynamicMessageEncodingAndDecoding() {
        val speed = 88.5f
        val gear = VehicleGear.DRIVE
        val power = PowerState.RUNNING
        val counter = 12

        val frame = DbcMessageMatrixDecoder.encodeVehicleDynamic(
            speedKmh = speed,
            gear = gear,
            powerState = power,
            counter = counter
        )

        val decoded = DbcMessageMatrixDecoder.decodeFrame(frame)
        assertTrue("Message must decode to VehicleDynamic", decoded is DbcDecodedMessage.VehicleDynamic)

        val dynamicMsg = decoded as DbcDecodedMessage.VehicleDynamic
        assertEquals(speed, dynamicMsg.speedKmh, 0.01f)
        assertEquals(gear, dynamicMsg.gear)
        assertEquals(power, dynamicMsg.powerState)
        assertEquals(counter, dynamicMsg.counter)
        assertTrue("CRC8 must be valid", dynamicMsg.isCrcValid)
    }

    @Test
    fun testClimateStatusMessageEncodingAndDecoding() {
        val driverTemp = 22.5f
        val passengerTemp = 24.0f
        val fanSpeed = 5
        val isAcOn = true

        val frame = DbcMessageMatrixDecoder.encodeClimateStatus(
            driverTempCelsius = driverTemp,
            passengerTempCelsius = passengerTemp,
            fanSpeedLevel = fanSpeed,
            isAcOn = isAcOn
        )

        val decoded = DbcMessageMatrixDecoder.decodeFrame(frame)
        assertTrue("Message must decode to ClimateStatus", decoded is DbcDecodedMessage.ClimateStatus)

        val climateMsg = decoded as DbcDecodedMessage.ClimateStatus
        assertEquals(driverTemp, climateMsg.driverTempCelsius, 0.1f)
        assertEquals(passengerTemp, climateMsg.passengerTempCelsius, 0.1f)
        assertEquals(fanSpeed, climateMsg.fanSpeedLevel)
        assertEquals(isAcOn, climateMsg.isAcOn)
    }

    @Test
    fun testBatteryEnergyMessageEncodingAndDecoding() {
        val soc = 78.4f
        val voltage = 395.2f
        val current = -25.5f // Discharge current
        val range = 420

        val frame = DbcMessageMatrixDecoder.encodeBatteryEnergy(
            batterySocPercent = soc,
            batteryVoltageVolts = voltage,
            batteryCurrentAmps = current,
            remainingRangeKm = range
        )

        val decoded = DbcMessageMatrixDecoder.decodeFrame(frame)
        assertTrue("Message must decode to BatteryEnergy", decoded is DbcDecodedMessage.BatteryEnergy)

        val energyMsg = decoded as DbcDecodedMessage.BatteryEnergy
        assertEquals(soc, energyMsg.batterySocPercent, 0.1f)
        assertEquals(voltage, energyMsg.batteryVoltageVolts, 0.1f)
        assertEquals(current, energyMsg.batteryCurrentAmps, 0.1f)
        assertEquals(range, energyMsg.remainingRangeKm)
    }

    @Test
    fun testAdasStatusMessageEncodingAndDecoding() {
        val accActive = true
        val aebWarning = false
        val laneDeparture = true
        val dist = 35.8f

        val frame = DbcMessageMatrixDecoder.encodeAdasStatus(
            isAccActive = accActive,
            isAebWarning = aebWarning,
            isLaneDepartureWarning = laneDeparture,
            targetDistanceMeters = dist
        )

        val decoded = DbcMessageMatrixDecoder.decodeFrame(frame)
        assertTrue("Message must decode to AdasStatus", decoded is DbcDecodedMessage.AdasStatus)

        val adasMsg = decoded as DbcDecodedMessage.AdasStatus
        assertEquals(accActive, adasMsg.isAccActive)
        assertEquals(aebWarning, adasMsg.isAebWarning)
        assertEquals(laneDeparture, adasMsg.isLaneDepartureWarning)
        assertEquals(dist, adasMsg.targetDistanceMeters, 0.1f)
    }
}
