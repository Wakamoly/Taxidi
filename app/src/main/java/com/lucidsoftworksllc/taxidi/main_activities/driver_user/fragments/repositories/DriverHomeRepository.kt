package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import androidx.lifecycle.MutableLiveData
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverHomeAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverHomeRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverHomeAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    val userVerified = MutableLiveData<Int>()
    val numShipped = MutableLiveData<Int>()
    val userStatus = MutableLiveData<Int>()

    suspend fun getUserPrefBits() {
        userStatus.value = userPreferences.userStatus()
        userVerified.value = userPreferences.userVerified()
        numShipped.value = userPreferences.userNumShipped()
    }

}
