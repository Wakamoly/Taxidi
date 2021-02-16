package com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api

import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.others.models.server_responses.GenericServerResponseModel
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DriverMainAPI {

    /*
    $username = $app->request()->post('username');
    $user_id = $app->request()->post('user_id');
    $fcmtoken = $app->request()->post('fcm_token');
    $old_token = $app->request()->post('old_token');
    */
    @FormUrlEncoded
    @POST("users_main.php/storefcmtoken")
    suspend fun storeFCMToken(
        @Field("username") username: String?,
        @Field("user_id") user_id: Int?,
        @Field("fcm_token") fcm_token: String?,
        @Field("old_token") old_token: String?
    ) : GenericServerResponseModel

}
