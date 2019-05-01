package io.locally.discover

import io.locally.discover.settings.DiscoverSDKSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class DiscoverSDKSettingsTest {

    private lateinit var settings: DiscoverSDKSettings

    @Before
    fun init(){
        settings = DiscoverSDKSettings()
    }

    @Test
    fun getDefaultStream() {
        val stream = settings.defaultStream

        assertNotNull(stream)
        assertEquals("DiscoverSDKStream", stream)
    }

    @Test
    fun getMinTime() {
        val minTime = settings.minTime

        assertEquals(0, minTime)
    }

    @Test
    fun getMinDistance() {
        val minDistance = settings.minDistance

        assertEquals(0.0F, minDistance)
    }

    @Test
    fun getTimeout() {
        val timeout = settings.timeout

        assertEquals(5000, timeout)
    }

    @Test
    fun getDelay() {
        val delay = settings.delay

        assertEquals(5000, delay)
    }
}