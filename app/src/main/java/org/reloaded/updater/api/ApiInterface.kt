package org.reloaded.updater.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("checkupdates.php")
    fun checkupdates(@Field("device") device: String, @Field("device_id") device_id: String, @Field("build_date") build_date: String): Call<Response>
}