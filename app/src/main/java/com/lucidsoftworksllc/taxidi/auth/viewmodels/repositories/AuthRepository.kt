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
        email: String, password: String
    ) = safeApiCall {
        api.login(email, password)
    }

    // TODO: 1/30/2021 Call this from VM
    suspend fun saveCredentials(username: String, user_id: Int, type: String){
        userPreferences.saveCredentials(username, user_id, type)
    }

    suspend fun saveUser(registerModel: RegisterModel) = safeApiCall {
        api.register(
                registerModel.signInAs,
                registerModel.username,
                registerModel.emailAddress,
                registerModel.password,
                registerModel.authorityType,
                registerModel.type,
                registerModel.companyName,
                registerModel.streetAddress,
                registerModel.city,
                registerModel.state,
                registerModel.zipCode,
                registerModel.country,
                registerModel.companyPhone,
                registerModel.firstName,
                registerModel.lastName,
                registerModel.personalPhone
        )
    }

}
