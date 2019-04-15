package org.reloaded.updater.utils

import android.content.Context
import android.net.ConnectivityManager

class Common {
    // Checks internet access
    companion object {
        fun checkNetwork(context: Context): Boolean{
            val networkInfo = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}