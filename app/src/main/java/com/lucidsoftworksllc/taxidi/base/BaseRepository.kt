package com.lucidsoftworksllc.taxidi.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
                Result.Error(throwable.message, null)
            }
        }
    }

}
