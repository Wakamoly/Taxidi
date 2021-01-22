package com.udacity.project4.base

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class BaseRepository(
    private val dispatcher: CoroutineDispatcher
) : ReminderDataSource {

    suspend fun <T : Any> safeApiCall(
            apiCall: suspend () -> T
    ) : Result<T> {
        return withContext(dispatcher){
            try {
                Result.Success(apiCall.invoke())
            }catch(throwable: Throwable){
                Result.Error(throwable.message, null)
            }
        }
    }

}
