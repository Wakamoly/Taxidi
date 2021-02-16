package com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMainAPI
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.others.models.server_responses.GenericServerResponseModel
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverMainActivityRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverMainAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    suspend fun sendFCMTokenToServer(fcmToken: String) : Result<GenericServerResponseModel> {
        val userID = userPreferences.userID()
        val username = userPreferences.userUsername()
        val currentToken = userPreferences.fCMToken()
        return safeApiCall {
            api.storeFCMToken(username, userID, fcmToken, currentToken)
        }
    }

    suspend fun saveFCMTokenLocally(fcmToken: String) {
        userPreferences.saveFCMToken(fcmToken)
    }

}
