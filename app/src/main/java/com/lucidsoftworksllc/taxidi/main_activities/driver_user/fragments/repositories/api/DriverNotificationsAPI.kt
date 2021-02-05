package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api

import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverNotificationsResponseModel
import retrofit2.http.GET
import retrofit2.http.Query

interface DriverNotificationsAPI {

    @GET("user_notifications.php/load_notifications")
    suspend fun loadNotifications(
            @Query("userID") userID: Int?,
            @Query("last_id") last_id: Long?
    ): DriverNotificationsResponseModel

}
