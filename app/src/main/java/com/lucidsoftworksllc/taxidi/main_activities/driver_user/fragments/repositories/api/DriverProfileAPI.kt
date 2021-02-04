package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api

import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverProfileResponseModel
import retrofit2.http.GET
import retrofit2.http.Query

interface DriverProfileAPI {

    @GET("user_profile.php/get_profile_by_username")
    suspend fun getProfileByUsername(
        @Query("username") username: String?
    ): DriverProfileResponseModel

    @GET("user_profile.php/load_profile")
    suspend fun loadProfile(
        @Query("userID") userID: Int?
    ): DriverProfileResponseModel

}
