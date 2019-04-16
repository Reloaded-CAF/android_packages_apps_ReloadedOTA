package org.reloaded.updater.tasks

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.provider.Settings
import org.jetbrains.anko.toast
import org.reloaded.updater.api.ApiClient
import org.reloaded.updater.api.ApiInterface
import org.reloaded.updater.api.Response
import org.reloaded.updater.utils.Common
import retrofit2.Call
import retrofit2.Callback


class CheckUpdate(private val isBackground: Boolean, callback: UpdateCheckerCallback) : AsyncTask<Void, Void, Response>() {

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

        val device = android.os.Build.DEVICE
        val buildDate = Common.getBuildDate(callback?.callbackContext!!)
        val androidId = Settings.Secure.getString(callback.callbackContext.contentResolver, Settings.Secure.ANDROID_ID)
        val call = apiInterface?.checkupdates(device, androidId, buildDate)
        var updateResponse = Response()
        var responseReceived = false

        call?.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.body() != null) {
                    updateResponse = response.body()
                    responseReceived = true
                }
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