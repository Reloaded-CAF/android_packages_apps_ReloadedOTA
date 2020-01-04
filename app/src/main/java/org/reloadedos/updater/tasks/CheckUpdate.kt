package org.reloadedos.updater.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.provider.Settings
import java.text.SimpleDateFormat
import java.util.Date
import retrofit2.Call
import retrofit2.Callback

import org.reloadedos.updater.api.ApiClient
import org.reloadedos.updater.api.ApiInterface
import org.reloadedos.updater.api.Response
import org.reloadedos.updater.utils.Common

class CheckUpdate(private val isBackground: Boolean, callback: UpdateCheckerCallback) :
    AsyncTask<Void, Void, Response>() {

    private var apiInterface: ApiInterface? = null
    private val callback: UpdateCheckerCallback? = callback

    interface UpdateCheckerCallback {
        val callbackContext: Context
        fun processResult(response: Response)
    }

    init {
        apiInterface = ApiClient.apiClient.create(ApiInterface::class.java)
    }

    @SuppressLint("HardwareIds")
    override fun doInBackground(vararg params: Void?): Response {
        val device = Common.getDevice(callback?.callbackContext!!)
        val version = Common.getVersion(callback.callbackContext)
        val androidId = Settings.Secure.getString(
            callback.callbackContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val call = apiInterface?.checkupdates(device, androidId, version)
        var updateResponse = Response()
        var responseReceived = false

        val date = Date()
        val format = SimpleDateFormat("d MMM")
        val spe =
            callback.callbackContext.getSharedPreferences("reloaded_pref", Context.MODE_PRIVATE)
                .edit()
        spe.putString("last_check", format.format(date))
        spe.apply()

        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.body() != null) {
                    updateResponse = response.body()
                }
                responseReceived = true
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                responseReceived = true
            }
        })

        while (!responseReceived) Thread.sleep(1000)

        return updateResponse

    }

    override fun onPostExecute(result: Response?) {
        super.onPostExecute(result)

        callback?.processResult(result!!)
    }

}