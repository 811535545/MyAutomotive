package com.oem.car.core.audio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MultiZoneAudioManagerTest {

    @Test
    fun testDriverNavFocusDucksOnlyDriverZone() {
        val manager = MultiZoneAudioManager()

        // Driver Zone 0 requests Navigation focus
        val decision = manager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.NAVIGATION)
        assertTrue("Navigation request must be granted", decision is AudioFocusDecision.GrantedDuck)

        val statuses = manager.zoneStatuses.value

        // Driver zone 0 should duck to 0.3f
        assertEquals(0.3f, statuses[AudioZone.DRIVER_ZONE_0]?.volumeMultiplier ?: 0f, 0.01f)

        // Passenger zone 1 remains unaffected (1.0f - Physical Zone Isolation)
        assertEquals(1.0f, statuses[AudioZone.PASSENGER_ZONE_1]?.volumeMultiplier ?: 0f, 0.01f)
    }

    @Test
    fun testGlobalVoiceCommandDucksAllZones() {
        val manager = MultiZoneAudioManager()

        // Voice Command triggered
        val decision = manager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.VOICE_COMMAND)
        assertTrue("Voice command request must be granted with ducking", decision is AudioFocusDecision.GrantedDuck)

        val statuses = manager.zoneStatuses.value

        // All active zones ducked to 0.1f
        assertEquals(0.1f, statuses[AudioZone.DRIVER_ZONE_0]?.volumeMultiplier ?: 0f, 0.01f)
        assertEquals(0.1f, statuses[AudioZone.PASSENGER_ZONE_1]?.volumeMultiplier ?: 0f, 0.01f)
    }

    @Test
    fun testEmergencyAlarmOverridesAllZonesAndRejectsLowPriority() {
        val manager = MultiZoneAudioManager()

        // Emergency Alarm triggered
        val emergencyDecision = manager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.EMERGENCY_ALARM)
        assertTrue("Emergency alarm must be granted exclusive override", emergencyDecision is AudioFocusDecision.GrantedExclusive)

        val statuses = manager.zoneStatuses.value
        assertEquals(0.0f, statuses[AudioZone.DRIVER_ZONE_0]?.volumeMultiplier ?: 1.0f, 0.01f)

        // Attempt to request MEDIA focus during active emergency alarm -> Must be REJECTED
        val mediaDecision = manager.requestAudioFocus(AudioZone.DRIVER_ZONE_0, CarAudioUsage.MEDIA)
        assertTrue("Low-priority MEDIA must be rejected during emergency alarm", mediaDecision is AudioFocusDecision.Rejected)
    }

    @Test
    fun testAudioBusRoutingByUsage() {
        val manager = MultiZoneAudioManager()

        // Query AudioBus routing for NAVIGATION in DRIVER_ZONE_0
        val navBus = manager.getAudioBusForUsage(AudioZone.DRIVER_ZONE_0, CarAudioUsage.NAVIGATION)
        assertNotNull(navBus)
        assertEquals("bus1_nav_driver", navBus?.busName)

        // Query AudioBus routing for CALL in DRIVER_ZONE_0 (Headrest speaker)
        val callBus = manager.getAudioBusForUsage(AudioZone.DRIVER_ZONE_0, CarAudioUsage.CALL)
        assertNotNull(callBus)
        assertEquals("bus2_call_headrest", callBus?.busName)
    }
}
