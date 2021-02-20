package com.lucidsoftworksllc.taxidi.others

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverHomeRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverHomeAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException

class RefreshDataWork(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWork"
    }

    override suspend fun doWork(): Result {
        val driverHomeDao = TaxidiDatabase(applicationContext).getDriverHomeDao()
        val userPreferences = UserPreferences(applicationContext)
        val authToken = runBlocking { userPreferences.authToken() }

        val repository =  DriverHomeRepository(
            userPreferences, // Datastore
            RemoteDataSource().buildApi(DriverHomeAPI::class.java, authToken),
            driverHomeDao
        )

        return try {
            repository.getHomeInfoFromServer()
            Result.success()
        } catch (exception: HttpException) {
            Result.retry()
        }
    }

}