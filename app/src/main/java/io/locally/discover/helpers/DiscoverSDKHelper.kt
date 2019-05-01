package io.locally.discover.helpers

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.telephony.TelephonyManager
import android.webkit.WebView
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.locally.discover.BuildConfig
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object DiscoverSDKHelper {

    private lateinit var context: Context

    fun init(context: Context) {
        DiscoverSDKHelper.context = context
        doAsync {
            setGaid()
            setOPT()
        }
    }

    private fun setOPT() {
        try {
            optOut = AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled
        } catch(e: Exception){ e.printStackTrace() }
    }

    var gaid: String? = null

    var optOut: Boolean = false

    private fun setGaid() {
        try {
            gaid = AdvertisingIdClient.getAdvertisingIdInfo(context).id
        } catch (e: Exception) { e.printStackTrace() }
    }

    val app: String?
        get() = context.packageName

    val country: String?
        get() = context.resources.configuration.locale.country

    val deviceModel: String?
        get() = Build.MANUFACTURER + " : " + Build.MODEL

    val deviceVersion: String?
        get() = Build.VERSION.SDK_INT.toString()

    val ipv6: String?
        get() {
            try {
                val en = NetworkInterface.getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()

                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet6Address) {
                            return inetAddress.getHostAddress().split("%".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

    val ipv4: String?
        get() {
            try {
                val en = NetworkInterface
                        .getNetworkInterfaces()
                while (en.hasMoreElements()) {
                    val intf = en.nextElement()
                    val enumIpAddr = intf.inetAddresses
                    while (enumIpAddr.hasMoreElements()) {
                        val inetAddress = enumIpAddr.nextElement()
                        if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                            return inetAddress.getHostAddress()
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

    val os: String?
        get() {
            val builder = StringBuilder()
            builder.append("android: ").append(Build.VERSION.RELEASE)

            val fields = Build.VERSION_CODES::class.java.fields
            for (field in fields) {
                val fieldName = field.name
                var fieldValue = -1

                try {
                    fieldValue = field.getInt(Any())
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }

                if (fieldValue == Build.VERSION.SDK_INT) {
                    builder.append(" : ").append(fieldName)
                }
            }

            return builder.toString()
        }


    val ssid: String?
        get() {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            return info.ssid
        }

    val bssid: String?
        get() {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val info = wifiManager.connectionInfo
            return info.bssid
        }

    val network: String?
        get() {
            val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            var network = ""

            if (info != null && info.isConnected) {
                when (info.type) {
                    ConnectivityManager.TYPE_WIFI -> network = "Wifi"
                    ConnectivityManager.TYPE_MOBILE -> network = "Mobile"
                }
            }

            return network
        }

    val versionName: String?
        get() {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                return pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return ""
        }

    var bluetooth = false
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
            return mBluetoothAdapter.isEnabled
        }

    var isBtConnected = false
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
            return mBluetoothAdapter.bondedDevices.size > 0
        }

    val bluetoothName: String
        get() {
            val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return ""
            return mBluetoothAdapter.name
        }

    val carrierName: String
        get() {
            val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return manager.networkOperatorName
        }

    val battery: String?
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        get() {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            var batLevel = 0

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            }

            return batLevel.toString()
        }

    val deviceTimeStamp: Long = Date().time

    var manufacturer: String? = Build.MANUFACTURER

    val userAgent: String?
        get() = WebView(context).settings.userAgentString ?: ""

    val bluetoothDevices: List<String>
        get() {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

            return if (bluetoothAdapter != null && bluetoothAdapter.isEnabled){
                val pairedDevices = bluetoothAdapter.bondedDevices.map { it -> it.name }
                pairedDevices
            } else arrayListOf()
        }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        return sdf.format(timestamp)
    }

    fun json(): JSONObject {
        val json = JSONObject()

        json.put("app_name", app)
        json.put("advertiser_id", gaid)
        json.put("country", country)
        json.put("device_model", deviceModel)
        json.put("device_version", deviceVersion)
        json.put("ipv6", ipv6)
        json.put("ipv4", ipv4)
        json.put("device_os ", os)
        json.put("wifi_ssid", ssid)
        json.put("wifi_bssid", bssid)
        json.put("connection_type", network)
        json.put("version_name", versionName)
        json.put("bluetooth", bluetooth)
        json.put("bluetooth_name", bluetoothName)
        json.put("bluetooth_devices", isBtConnected)
        json.put("carrier_name", carrierName)
        json.put("batt_level", battery)
        json.put("device_timestamp", deviceTimeStamp)
        json.put("manufacturer", manufacturer)
        json.put("sdk_version", BuildConfig.VERSION_NAME)
        json.put("publisher_id", "")

        return json
    }
}