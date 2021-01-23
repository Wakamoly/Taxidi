package com.lucidsoftworksllc.taxidi.utils


/**
 * A sealed class that encapsulates successful outcome with a value of type [T]
 * or a failure with message and statusCode
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(var message: String?, val statusCode: Int? = null) :
        Result<Nothing>()
    object Loading : Result<Nothing>()
}