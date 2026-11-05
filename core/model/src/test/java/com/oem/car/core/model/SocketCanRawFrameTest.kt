package com.oem.car.core.model

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SocketCanRawFrameTest {

    @Test
    fun testSocketCanRawFrameBinarySerialization() {
        val payload = byteArrayOf(0x12, 0x34, 0x56, 0x78.toByte(), 0x90.toByte(), 0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
        val canFrame = CanFrame(
            id = 0x120,
            dlc = 8,
            payload = payload
        )

        val rawFrame = SocketCanRawFrame.fromCanFrame(canFrame, isExtended = false)
        assertEquals(0x120, rawFrame.canId)
        assertEquals(8, rawFrame.dlc)
        assertFalse("Must be standard 11-bit frame", rawFrame.isExtended)

        // Serialize to 16-byte C-struct buffer
        val rawBuffer = rawFrame.toRawBuffer()
        assertEquals(16, rawBuffer.size)

        // Parse back from 16-byte binary buffer
        val parsedFrame = SocketCanRawFrame.parseFromBuffer(rawBuffer)
        assertEquals(0x120, parsedFrame.canId)
        assertEquals(8, parsedFrame.dlc)
        assertArrayEquals(payload, parsedFrame.data)

        // Convert back to CanFrame
        val restoredCanFrame = parsedFrame.toCanFrame()
        assertEquals(canFrame.id, restoredCanFrame.id)
        assertEquals(canFrame.dlc, restoredCanFrame.dlc)
        assertArrayEquals(canFrame.payload, restoredCanFrame.payload)
    }

    @Test
    fun testExtendedCanIdFlags() {
        val extId = 0x18FEF100 // Extended 29-bit CAN ID (e.g., J1939 format)
        val payload = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val canFrame = CanFrame(id = extId, dlc = 8, payload = payload)

        val rawFrame = SocketCanRawFrame.fromCanFrame(canFrame, isExtended = true)
        assertTrue("EFF flag must be set", rawFrame.isExtended)
        assertEquals(extId, rawFrame.canId)

        val buffer = rawFrame.toRawBuffer()
        val parsed = SocketCanRawFrame.parseFromBuffer(buffer)
        assertTrue(parsed.isExtended)
        assertEquals(extId, parsed.canId)
    }
}
