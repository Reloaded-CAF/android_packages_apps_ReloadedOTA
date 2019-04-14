package org.reloaded.updater.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://fabdevs.com/reloaded/"
    private var retrofit: Retrofit? = null
    val apiClient: Retrofit
        get() {
            if (retrofit == null) {
                retrofit =
                    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit!!
        }
}
