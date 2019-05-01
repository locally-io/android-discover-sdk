package io.locally.discover

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.util.Log
import com.amazonaws.mobileconnectors.kinesis.kinesisrecorder.KinesisFirehoseRecorder
import com.amazonaws.regions.Regions
import io.locally.discover.helpers.DiscoverSDKHelper
import io.locally.discover.settings.DiscoverSDKSettings
import io.locally.discover.authentication.managers.AuthenticationManager
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.*
import kotlin.concurrent.timerTask

@SuppressLint("StaticFieldLeak", "MissingPermission")
object DiscoverSDK {
    private val TAG: String = "SDKLite"

    private var locationManager: LocationManager? = null
    private var delegate: DiscoverSDKDelegate? = null

    private lateinit var settings: DiscoverSDKSettings
    private lateinit var context: Context

    /* timer */
    private var timer = Timer()
    private var task = timerTask { postData() }

    /* recorder */
    private lateinit var fireHoseRecorder: KinesisFirehoseRecorder

    /* location records*/
    private var locations = JSONArray()

    private val dataQualityAnalyser = DataQualityAnalyser()
    private lateinit var authenticator: AuthenticationManager

    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION])
    fun monitor(delegate: DiscoverSDKDelegate? = null, settings: DiscoverSDKSettings = DiscoverSDKSettings()) {
        require( authenticator.isLogged ) { "Please login first" }

        DiscoverSDK.delegate = delegate
        DiscoverSDK.settings = settings

        fireHoseRecorder = KinesisFirehoseRecorder(context.cacheDir, Regions.US_WEST_2, authenticator.helper?.credentialProvider)

        startMonitoring()
    }

    fun init(context: Context) {
        this.context = context
        DiscoverSDKHelper.init(context)
        authenticator = AuthenticationManager(context)
    }

    fun login(username: String, password: String, callback: ((Boolean) -> Unit)?) {
        require(::authenticator.isInitialized) { "Please init SDK" }

        authenticator.login(username, password, callback)
    }

    private fun startMonitoring() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        try {
            locationManager?.let {
                it.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, settings.minTime, settings.minDistance, locationListener)
                scheduleTimer()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun scheduleTimer() {
        timer.schedule(task, settings.delay, settings.timeout)
    }

    private fun postData() {
        delegate?.let {
            delegate?.didReachThreshold()
            if (it.shouldSendRecords){
                try {
                    fireHoseRecorder.submitAllRecords()
                    it.didUpdateRecords(locations)
                }catch (ex: Exception){
                    it.didReachThresholdWithError(ex)
                }
            }
        }
    }

    private val locationListener: LocationListener = object: LocationListener {

        override fun onLocationChanged(location: Location) {

            val locationInfo = JSONObject()
            val dataQualityIndex = dataQualityAnalyser.dataQuality(location)

            with(locationInfo) {
                put("latitude", location.latitude)
                put("longitude", location.longitude)
                put("altitude", location.altitude)
                put("heading", location.bearing)
                put("speed", location.speed)
                put("horizontal_accuracy", location.accuracy)
                put("data_quality", dataQualityIndex)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    put("mock", location.isFromMockProvider) //API 18+ required

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    put("heading_accuracy", location.bearingAccuracyDegrees)
                    put("vertical_accuracy", location.verticalAccuracyMeters)
                } //API 26+ required

                put("app_name", DiscoverSDKHelper.app)
                put("advertiser_id", DiscoverSDKHelper.gaid)
                put("country", DiscoverSDKHelper.country)
                put("device_model", DiscoverSDKHelper.deviceModel)
                put("device_version", DiscoverSDKHelper.deviceVersion)
                put("ipv6", DiscoverSDKHelper.ipv6)
                put("ipv4", DiscoverSDKHelper.ipv4)
                put("device_os", DiscoverSDKHelper.os)
                put("wifi_ssid", DiscoverSDKHelper.ssid)
                put("wifi_bssid", DiscoverSDKHelper.bssid)
                put("connection_type", DiscoverSDKHelper.network)
                put("version_name", DiscoverSDKHelper.versionName)
                put("bluetooth_enabled", DiscoverSDKHelper.bluetooth)
                put("bluetooth_name", DiscoverSDKHelper.bluetoothName)
                put("bluetooth_connected", DiscoverSDKHelper.isBtConnected)
                put("carrier_name", DiscoverSDKHelper.carrierName)
                put("batt_level", DiscoverSDKHelper.battery)
                put("device_timestamp", DiscoverSDKHelper.deviceTimeStamp)
                put("utc_timestamp", DiscoverSDKHelper.formatDate(location.time))
                put("local_timestamp", DateFormat.getDateInstance().format(location.time))
                put("manufacturer", DiscoverSDKHelper.manufacturer)
                put("user_agent", DiscoverSDKHelper.userAgent)
                put("bluetooth_devices", DiscoverSDKHelper.bluetoothDevices)
                put("opt_out", DiscoverSDKHelper.optOut)
                put("event_type", "GPS")
                put("sdk_version", BuildConfig.VERSION_NAME)
                put("publisher_id", authenticator.publisher)
            }

            delegate?.didUpdateDataQuality(dataQualityIndex)

            locations.put(locationInfo)
            saveRecord(locationInfo)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {}

        override fun onProviderDisabled(provider: String?) {}

    }

    private fun saveRecord(locationInfo: JSONObject) {

        try {
            delegate?.dataToIncludeOnRecords(locationInfo)
            fireHoseRecorder.saveRecord(locationInfo.toString(), settings.defaultStream)
        } catch (ex: Exception) {
            Log.e(TAG, "Error submitting record", ex)
            delegate?.didUpdateRecordsWithError(ex)
        }
    }
}