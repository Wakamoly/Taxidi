package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverProfileAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverProfileRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverProfileAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    suspend fun getProfileID(username: String) = safeApiCall {
        api.getProfileByUsername(username)
    }

    suspend fun loadProfile(userId: Int) = safeApiCall {
        api.loadProfile(userId)
    }


}
