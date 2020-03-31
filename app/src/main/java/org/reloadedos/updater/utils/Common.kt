package org.reloadedos.updater.utils

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
            val buildVersion = this.getBuildVersion(context)
            val outputFormat = SimpleDateFormat("d MMM yyyy")
            return if (buildVersion.equals("unknown")) {
                val dateObj = Date(Build.TIME)
                outputFormat.format(dateObj)
            } else {
                val buildDate = buildVersion.split("-")[3]
                val format = SimpleDateFormat("yyyyMMdd")
                val dateObj = format.parse(buildDate)
                outputFormat.format(dateObj)
            }
        }

        fun getBuildVersion(context: Context): String {
            return SystemPropertiesProxy[context, "ro.reloaded.version", "unknown"]
        }

        fun getDeviceName(context: Context): String {
            return SystemPropertiesProxy[context, "ro.reloaded.device", "unknown"]
        }
    }
}