package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.db.dao.DriverHomeDao
import com.lucidsoftworksllc.taxidi.db.entities.asDomainModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverHomeAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverHomeViewResponseModel
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriverHomeRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverHomeAPI,
    private val dao: DriverHomeDao,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    val userVerified = MutableLiveData<Int>()
    val numShipped = MutableLiveData<Int>()
    val userStatus = MutableLiveData<Int>()
    val logList: LiveData<List<DriverHomeViewResponseModel.LogResult>> = Transformations.map(dao.getHomeLogs()) {
        it?.asDomainModel()
    }
    val newsList: LiveData<List<DriverHomeViewResponseModel.NewsResult>> = Transformations.map(dao.getHomeNews()) {
        it?.asDomainModel()
    }

    suspend fun getUserPrefBits() {
        userStatus.value = userPreferences.userStatus()
        userVerified.value = userPreferences.userVerified()
        numShipped.value = userPreferences.userNumShipped()
    }

    suspend fun getHomeInfoFromServer() : Result<DriverHomeViewResponseModel> {
        val username = userPreferences.userUsername()
        val userID = userPreferences.userID()
        val resultFromServer = safeApiCall { api.getHomeInfo(userID, username, dao.getLogLastID().toInt(), dao.getNewsLastID().toInt()) }
        if (resultFromServer is Result.Success && !resultFromServer.data.error) {
            val result = resultFromServer.data
            if (result.log_result != null) {
                for (log in result.log_result) {
                    dao.insertLog(log.asDatabaseModel())
                }
            }
            if (result.news_result != null) {
                for (news in result.news_result) {
                    dao.insertNews(news.asDatabaseModel())
                }
            }
            if (result.top_result != null) {
                userPreferences.setHomeViewValues(result.top_result.verified, result.top_result.numshipped)
            }
        }
        getUserPrefBits()
        return resultFromServer
    }

}
