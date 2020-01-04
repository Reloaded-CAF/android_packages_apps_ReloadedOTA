package org.reloaded.updater.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date

class Common {
    companion object {
        fun checkNetwork(context: Context): Boolean {
            val networkInfo =
                (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun getBuildDate(context: Context): String {
            val version = this.getVersion(context)
            val outputFormat = SimpleDateFormat("d MMM yyyy")
            if (version.equals("unknown")) {
                val dateObj = Date(Build.TIME)
                return outputFormat.format(dateObj)
            } else {
                val buildDate = version.split("-")[3]
                val format = SimpleDateFormat("yyyyMMdd")
                val dateObj = format.parse(buildDate)
                return outputFormat.format(dateObj)
            }
        }

        fun getVersion(context: Context): String {
            return SystemPropertiesProxy[context, "ro.reloaded.version", "unknown"]
        }

        fun getDevice(context: Context): String {
            return SystemPropertiesProxy[context, "ro.reloaded.device", "unknown"]
        }
    }
}