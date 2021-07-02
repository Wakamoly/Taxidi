package com.lucidsoftworksllc.taxidi.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.lucidsoftworksllc.taxidi.utils.Result as Result

abstract class BaseRepository(
    private val dispatcher: CoroutineDispatcher
)  {

    suspend fun <T : Any> safeApiCall(
            apiCall: suspend () -> T
    ) : Result<T> {
        return withContext(dispatcher){
            try {
                Result.Success(apiCall.invoke())
            }catch(throwable: Throwable){
                when (throwable) {
                    is HttpException -> {
                        Result.Error(throwable.message, throwable.code())
                    }
                    else -> {
                        Result.Error(throwable.message, null)
                    }
                }
            }
        }
    }

    suspend fun logout(api: UserAPI, username: String, userID: Int, fcmToken: String) = safeApiCall {
        api.logout(username, userID, fcmToken)
    }

}
