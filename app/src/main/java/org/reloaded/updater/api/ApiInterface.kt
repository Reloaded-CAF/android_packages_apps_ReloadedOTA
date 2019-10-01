package org.reloaded.updater.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("check_update")
    fun checkupdates(@Field("device") device: String, @Field("device_id") device_id: String, @Field("version") version: String): Call<Response>
}
