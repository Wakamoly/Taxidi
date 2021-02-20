package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.db.dao.DriverNotificationDao
import com.lucidsoftworksllc.taxidi.db.entities.asDomainModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverNotificationsAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverNotificationsResponseModel
import com.lucidsoftworksllc.taxidi.others.models.server_responses.ProfileNotification
import com.lucidsoftworksllc.taxidi.others.models.server_responses.asDatabaseModel
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverNotificationsRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverNotificationsAPI,
    private val notificationDao: DriverNotificationDao,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    /**
     * Return DB list of notification data as LiveData
     */
    val notifications: LiveData<List<ProfileNotification>> = Transformations.map(notificationDao.getNotifications()) {
        it?.asDomainModel()
    }

    suspend fun refreshNotifications() : Result<DriverNotificationsResponseModel> {
        val apiResult = safeApiCall {
            api.loadNotifications(userPreferences.userID(), notificationDao.getLastID())
        }
        if (apiResult is Result.Success){
            if (!apiResult.data.error){
                apiResult.data.result?.apply {
                    notificationDao.insertAll(this.asDatabaseModel())
                }
            }
        }
        return apiResult
    }

    suspend fun setAllNotificationsRead() {
        safeApiCall {
            notificationDao.setAllOpened()
        }
    }

    suspend fun setSingleNotificationRead(id: Long) {
        safeApiCall {
            notificationDao.setOpened(id)
        }
    }

}
