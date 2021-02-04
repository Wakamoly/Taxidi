package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverMapRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverMapAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

}
