package org.reloaded.updater.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("checkupdates.php")
    Call<Response> checkupdates(@Field("device") String device, @Field("device_id") String device_id, @Field("build_date") String build_date);
}