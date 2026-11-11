package com.oem.car.core.model

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CanFrameTest {

    @Test
    fun testDynamicSpeedAndGearEncodingDecoding() {
        val testCases = listOf(
            Triple(0.0f, VehicleGear.PARK, PowerState.SHUTDOWN_PREPARE),
            Triple(15.5f, VehicleGear.REVERSE, PowerState.RUNNING),
            Triple(60.0f, VehicleGear.DRIVE, PowerState.RUNNING),
            Triple(120.0f, VehicleGear.DRIVE, PowerState.RUNNING),
            Triple(250.0f, VehicleGear.NEUTRAL, PowerState.STR_SLEEP)
        )

        testCases.forEachIndexed { index, (speed, gear, power) ->
            val counter = index % 16
            val frame = DbcMessageMatrixDecoder.encodeVehicleDynamic(
                speedKmh = speed,
                gear = gear,
                powerState = power,
                counter = counter
            )

            val decoded = DbcMessageMatrixDecoder.decodeFrame(frame)
            assertNotNull("Decoded frame should not be null", decoded)
            assertTrue(decoded is DbcDecodedMessage.VehicleDynamic)

            val dynamicMsg = decoded as DbcDecodedMessage.VehicleDynamic
            assertEquals("Speed should match", speed, dynamicMsg.speedKmh, 0.01f)
            assertEquals("Gear should match", gear, dynamicMsg.gear)
            assertEquals("Power state should match", power, dynamicMsg.powerState)
            assertEquals("Counter should match", counter, dynamicMsg.counter)
            assertTrue("CRC8 must be valid", dynamicMsg.isCrcValid)
        }
    }

    @Test
    fun testRollingCounterRollover() {
        // Rolling counter cycles from 0 to 15 and wraps around
        for (c in 0..32) {
            val expectedCounter = c % 16
            val frame = CanMessageCodec.encodeVehicleSignal(
                speed = 80.0f,
                gear = 8,
                powerState = 0,
                counter = c
            )

            val decoded = CanMessageCodec.decodeVehicleSignal(frame)
            assertNotNull(decoded)
            assertEquals(expectedCounter, decoded!!.counter)
            assertTrue(decoded.isCrcValid)
        }
    }

    @Test
    fun testCorruptedPayloadCrcFailure() {
        val validFrame = CanMessageCodec.encodeVehicleSignal(
            speed = 100.0f,
            gear = 8,
            powerState = 0,
            counter = 3
        )

        // Corrupt Byte 0 (speed byte)
        val corruptedPayload = validFrame.payload.clone()
        corruptedPayload[0] = (corruptedPayload[0] + 1).toByte()

        val corruptedFrame = validFrame.copy(payload = corruptedPayload)
        val decoded = CanMessageCodec.decodeVehicleSignal(corruptedFrame)

        assertNotNull("Corrupted frame still parses payload structure", decoded)
        assertFalse("CRC8 checksum must fail on corrupted payload", decoded!!.isCrcValid)
    }

    @Test
    fun testCanFrameHexDumpFormatting() {
        val payload = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val frame = CanFrame(id = 0x120, dlc = 8, payload = payload)

        val dump = frame.toHexDump()
        assertEquals("CAN[0x120] DLC=8 Data=01 02 03 04 05 06 07 08", dump)
    }

    @Test
    fun testCanFrameEqualsAndHashCode() {
        val payload1 = byteArrayOf(0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80.toByte())
        val payload2 = byteArrayOf(0x10, 0x20, 0x30, 0x40, 0x50, 0x60, 0x70, 0x80.toByte())
        val payload3 = byteArrayOf(0xFF.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)

        val time = 1000000L
        val frame1 = CanFrame(id = 0x120, dlc = 8, payload = payload1, timestampMs = time)
        val frame2 = CanFrame(id = 0x120, dlc = 8, payload = payload2, timestampMs = time)
        val frame3 = CanFrame(id = 0x120, dlc = 8, payload = payload3, timestampMs = time)

        assertEquals("Identical CAN frames must be equal", frame1, frame2)
        assertEquals("Identical CAN frames must have same hashCode", frame1.hashCode(), frame2.hashCode())
        assertNotEquals("Different CAN frames must not be equal", frame1, frame3)
    }

    @Test
    fun testDynamicCanFrameStreamProcessing() = runBlocking {
        // Simulate a dynamic 50Hz CAN stream with accelerating speed
        val frameStream = (0..50).asFlow().map { step ->
            val speed = step * 2f // 0 to 100 km/h
            val counter = step % 16
            DbcMessageMatrixDecoder.encodeVehicleDynamic(
                speedKmh = speed,
                gear = if (speed > 0f) VehicleGear.DRIVE else VehicleGear.PARK,
                powerState = PowerState.RUNNING,
                counter = counter
            )
        }

        val decodedMessages = frameStream
            .map { frame -> DbcMessageMatrixDecoder.decodeFrame(frame) }
            .toList()

        assertEquals(51, decodedMessages.size)
        decodedMessages.forEachIndexed { step, msg ->
            assertTrue(msg is DbcDecodedMessage.VehicleDynamic)
            val dynamicMsg = msg as DbcDecodedMessage.VehicleDynamic
            assertEquals(step * 2f, dynamicMsg.speedKmh, 0.01f)
            assertTrue(dynamicMsg.isCrcValid)
        }
    }
}
