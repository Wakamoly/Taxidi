package com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories

import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api.RegisterAPI
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.others.models.RegisterModel
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AuthRepository(
    private val userPreferences: UserPreferences,
    private val api: RegisterAPI,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher) {

    suspend fun login(
        email: String, username: String
    ) {
        // Database operations or network calls
        saveCredentials(username, email)
    }

    private suspend fun saveCredentials(username: String, email: String){
        userPreferences.saveCredentials(username, email)
    }

    suspend fun saveUser(registerModel: RegisterModel) = safeApiCall {
        // TODO: 1/28/2021  
        //api.
    }

}
