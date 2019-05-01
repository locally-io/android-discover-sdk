package io.locally.discover.aws

import android.support.test.InstrumentationRegistry
import io.locally.discover.aws.AWSHelper
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AWSConnectorTest {
    lateinit var aws: AWSHelper

    @Before
    fun setUp() {
        aws = AWSHelper(InstrumentationRegistry.getTargetContext())
        Thread.sleep(300)
    }

    @Test
    fun getProvider() {
        val provider = aws.credentialProvider

        assertNotNull(provider)
    }
}