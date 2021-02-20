package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api

import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverHomeViewResponseModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DriverHomeAPI {

    @FormUrlEncoded
    @POST("users_main.php/gethomeinfo")
    suspend fun getHomeInfo(
        @Field("user_id") userID: Int?,
        @Field("username") username: String?,
        @Field("last_news_id") last_news_id: Int?,
        @Field("last_log_id") last_log_id: Int?
    ) : DriverHomeViewResponseModel

}