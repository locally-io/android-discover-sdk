package io.locally.discover.settings

data class DiscoverSDKSettings(var defaultStream: String = "DiscoverSDKStream",
                               var minTime: Long = 0,
                               var minDistance: Float = 0F,
                               var timeout: Long = 5000,
                               var delay: Long = timeout)