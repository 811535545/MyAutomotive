package com.oem.car.core.vehicleservice

import org.junit.Assert.assertTrue
import org.junit.Test

class VehicleServiceTest {

    @Test
    fun testSocketCanAdapterTypeAliasResolution() {
        val adapter = SocketCanChannelAdapter("vcan0")
        assertTrue(adapter.channelName == "vcan0")
    }
}
