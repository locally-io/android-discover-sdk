package io.locally.discover.helpers

import android.content.Context
import android.support.test.InstrumentationRegistry
import io.locally.discover.helpers.DiscoverSDKHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DiscoverSDKHelperTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        DiscoverSDKHelper.init(context)
        Thread.sleep(300) //time to finish requesting async values
    }

    @Test
    fun getGaid() {
        assertNotNull(DiscoverSDKHelper.gaid)
    }

    @Test
    fun getApp() {
        assertNotNull(DiscoverSDKHelper.app)
    }

    @Test
    fun getCountry() {
        assertNotNull(DiscoverSDKHelper.country)
    }

    @Test
    fun getDevice_model() {
        assertNotNull(DiscoverSDKHelper.deviceModel)
    }

    @Test
    fun getDevice_version() {
        assertNotNull(DiscoverSDKHelper.deviceVersion)
    }

    @Test
    fun getIpv6() {
        assertNotNull(DiscoverSDKHelper.ipv6)
    }

    @Test
    fun getIpv4() {
        assertNotNull(DiscoverSDKHelper.ipv4)
    }

    @Test
    fun getOs() {
        assertNotNull(DiscoverSDKHelper.os)
    }

    @Test
    fun getSsid() {
        assertNotNull(DiscoverSDKHelper.ssid)
    }

    @Test
    fun getBssid() {
        assertNotNull(DiscoverSDKHelper.bssid)
    }

    @Test
    fun getNetwork() {
        assertNotNull(DiscoverSDKHelper.network)
    }

    @Test
    fun getVersion_name() {
        assertNull(DiscoverSDKHelper.versionName)
    }

    @Test
    fun getBluetooth() {
        assertFalse(DiscoverSDKHelper.bluetooth)
    }

    @Test
    fun isBtConnected() {
        assertFalse(DiscoverSDKHelper.isBtConnected)
    }

    @Test
    fun getBluetooth_name() {
        assertNotNull(DiscoverSDKHelper.bluetoothName)
    }

    @Test
    fun getCarrier_mame() {
        assertNotNull(DiscoverSDKHelper.carrierName)
    }

    @Test
    fun getBattery() {
        assertNotNull(DiscoverSDKHelper.battery)
    }

    @Test
    fun getUtc_time() {
        assertNotNull(DiscoverSDKHelper.deviceTimeStamp)
    }

    @Test
    fun json() {
        assertNotNull(DiscoverSDKHelper.json())
    }
}