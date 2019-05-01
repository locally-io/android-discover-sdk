package io.locally.discover

import org.json.JSONArray
import org.json.JSONObject

interface DiscoverSDKDelegate {
    var shouldSendRecords: Boolean

    fun didUpdateRecords(records: JSONArray) {}
    fun didReachThreshold() {}

    //optionals
    fun didUpdateDataQuality(quality: Double){}
    fun dataToIncludeOnRecords(record: JSONObject){}
    fun didUpdateRecordsWithError(error: Exception){}
    fun didReachThresholdWithError(error: Exception){}
}