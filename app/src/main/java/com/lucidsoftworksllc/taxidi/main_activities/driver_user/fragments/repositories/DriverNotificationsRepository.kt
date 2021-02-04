package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverNotificationsAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverNotificationsRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverNotificationsAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

}
