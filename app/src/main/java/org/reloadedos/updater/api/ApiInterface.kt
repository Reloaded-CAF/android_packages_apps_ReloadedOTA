package org.reloadedos.updater.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @FormUrlEncoded
    @POST("device/{device_codename}/check_update")
    fun checkUpdate(@Path("device_codename") device: String, @Field("device_id") device_id: String, @Field("build_version") build_version: String): Call<Response>
}
