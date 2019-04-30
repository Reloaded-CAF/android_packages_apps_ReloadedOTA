package org.reloaded.updater.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.SimpleDateFormat

class Common {
    companion object {
        fun checkNetwork(context: Context): Boolean{
            val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }

        fun getBuildDate(context: Context) : String{
            val version = SystemPropertiesProxy[context, "ro.reloaded.version"]
            return when(version != ""){
                true -> version.split("-")[3]
                false -> "20190101"
            }
        }

        fun getBuildDateFull(context: Context) : String{
            val date = SystemPropertiesProxy[context, "ro.build.date"]
            val format = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
            val dateObj = format.parse(date)
            val simpleDateFormat = SimpleDateFormat("d MMMM yyyy")

            return simpleDateFormat.format(dateObj)
        }

        fun getVersion(context: Context) : String{
            return SystemPropertiesProxy[context, "ro.reloaded.version","unknown"]
        }

        fun getDevice(context: Context) : String{
            return SystemPropertiesProxy[context, "ro.reloaded.device","unknown"]
        }
    }
}