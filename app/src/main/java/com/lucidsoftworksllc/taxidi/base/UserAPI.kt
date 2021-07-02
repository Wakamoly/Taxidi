package com.lucidsoftworksllc.taxidi.base

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {

    /*$username = $app->request()->post('username');
    $user_id = $app->request()->post('user_id');
    $token = $app->request()->post('token');*/
    // Deactivate FCM token on server
    @FormUrlEncoded
    @POST("users_main.php/removefcmtoken")
    suspend fun logout(
            @Field("username") username: String?,
            @Field("user_id") user_id: Int?,
            @Field("token") fcm_token: String?
    )

}
