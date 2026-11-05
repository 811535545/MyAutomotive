package com.oem.car.core.model

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SocketCanChannelAdapterTest {

    @Test
    fun testSocketCanPipelineRawBufferInjectionAndDbcDecoding() = runBlocking {
        val adapter = SocketCanChannelAdapter("vcan0", this)

        val canFrame = DbcMessageMatrixDecoder.encodeVehicleDynamic(
            speedKmh = 120.0f,
            gear = VehicleGear.DRIVE,
            powerState = PowerState.RUNNING,
            counter = 7
        )

        val rawFrame = SocketCanRawFrame.fromCanFrame(canFrame)
        val rawBuffer = rawFrame.toRawBuffer()

        // Inject raw 16-byte binary buffer into SocketCAN pipeline
        adapter.injectRawBuffer(rawBuffer)

        val decodedMsg = adapter.decodedMessageStream.first()
        assertTrue("Decoded message must be VehicleDynamic", decodedMsg is DbcDecodedMessage.VehicleDynamic)

        val dynamicMsg = decodedMsg as DbcDecodedMessage.VehicleDynamic
        assertEquals(120.0f, dynamicMsg.speedKmh, 0.01f)
        assertEquals(VehicleGear.DRIVE, dynamicMsg.gear)
        assertEquals(PowerState.RUNNING, dynamicMsg.powerState)
    }

    @Test
    fun testAcceptanceFilterBlocksUnwantedCanIds() = runBlocking {
        val adapter = SocketCanChannelAdapter("vcan0", this)

        // Set filter to only allow Climate CAN ID 0x210
        adapter.setAcceptanceFilter(DbcDecodedMessage.CAN_ID_CLIMATE_STATUS)

        val vehicleDynamicFrame = DbcMessageMatrixDecoder.encodeVehicleDynamic(
            speedKmh = 60.0f,
            gear = VehicleGear.DRIVE,
            powerState = PowerState.RUNNING,
            counter = 1
        )

        val climateFrame = DbcMessageMatrixDecoder.encodeClimateStatus(
            driverTempCelsius = 22.0f,
            passengerTempCelsius = 23.0f,
            fanSpeedLevel = 3,
            isAcOn = true
        )

        // Inject vehicle dynamic (should be filtered out by acceptance filter)
        adapter.injectCanFrame(vehicleDynamicFrame)

        // Inject climate frame (should pass filter)
        adapter.injectCanFrame(climateFrame)

        val receivedMsg = adapter.decodedMessageStream.first()
        assertEquals(DbcDecodedMessage.CAN_ID_CLIMATE_STATUS, receivedMsg.canId)
        assertTrue("Received message must be ClimateStatus", receivedMsg is DbcDecodedMessage.ClimateStatus)
    }
}
