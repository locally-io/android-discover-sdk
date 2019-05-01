package io.locally.discover

import android.Manifest
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.rule.GrantPermissionRule
import io.locally.discover.DiscoverSDK
import io.locally.discover.DiscoverSDKDelegate
import org.json.JSONArray
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DiscoverSDKTest {

    private lateinit var context: Context

    @get:Rule var fineLocationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule val coarseLocationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION)

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
    }

    @Test
    fun connect() {
        val delegate = object : DiscoverSDKDelegate {
            override var shouldSendRecords: Boolean = true

            override fun didUpdateRecords(records: JSONArray) {}

            override fun didReachThreshold() {}
        }
        DiscoverSDK.init(context)

        val result = DiscoverSDK.monitor(delegate)

        assertEquals(true, result)
    }
}