package org.reloadedos.updater.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object ApiClient {
    private const val BASE_URL = "https://api.reloadedos.org/"
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
