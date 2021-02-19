package com.lucidsoftworksllc.taxidi.others.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferences (
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val dataStore: DataStore<Preferences> = applicationContext.createDataStore(
        name = STORE_NAME
    )

    private val username: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USERNAME]
        }

    private val userType: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_TYPE]
        }

    private val userID: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_ID]
        }

    private val userStatus: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_STATUS]
        }

    private val userVerified: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_VERIFIED]
        }

    private val userNumShipped: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_NUM_SHIPPED]
        }

    private val fCMToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_FCMTOKEN]
        }

    private val authToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }

    private val isUserLoggedIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_LOGGED_IN]
        }

    suspend fun saveCredentials(username: String, userID: Int, type: String, authToken: String){
        dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
            preferences[KEY_USER_ID] = userID
            preferences[KEY_USER_TYPE] = type
            preferences[KEY_USER_LOGGED_IN] = true
            preferences[KEY_USER_STATUS] = UserStatus.IDLE.out
            preferences[KEY_AUTH_TOKEN] = authToken
        }
        Log.d("UserPreferences", "saveCredentials: isUserLoggedIn -> ${isUserLoggedIn()}")
        Log.d("UserPreferences", "saveCredentials: userID -> ${userID()}")
        Log.d("UserPreferences", "saveCredentials: userUsername -> ${userUsername()}")
        Log.d("UserPreferences", "saveCredentials: userType -> ${userType()}")
        Log.d("UserPreferences", "saveCredentials: userStatus -> ${userStatus()}")
        Log.d("UserPreferences", "saveCredentials: authToken -> ${authToken()}")
    }

    suspend fun saveFCMToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_FCMTOKEN] = token
        }
        Log.d("UserPreferences", "saveFCMToken: FCMToken updated -> ${fCMToken()})")
    }

    suspend fun isUserLoggedIn(): Boolean = isUserLoggedIn.first() ?: false

    suspend fun userID(): Int = userID.first() ?: 0

    suspend fun userStatus(): Int = userStatus.first() ?: 0

    suspend fun userVerified(): Int = userVerified.first() ?: 0

    suspend fun userNumShipped(): Int = userNumShipped.first() ?: 0

    suspend fun userUsername(): String = username.first().toString()

    suspend fun userType(): String = userType.first().toString()

    suspend fun fCMToken(): String = fCMToken.first().toString()

    suspend fun authToken(): String = authToken.first().toString()

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_USERNAME = stringPreferencesKey("key_username")
        private val KEY_USER_ID = intPreferencesKey("key_user_id")
        private val KEY_USER_STATUS = intPreferencesKey("key_user_status")
        private val KEY_USER_TYPE = stringPreferencesKey("key_user_type")
        private val KEY_USER_LOGGED_IN = booleanPreferencesKey("key_is_user_logged_in")
        private val KEY_FCMTOKEN = stringPreferencesKey("key_fcm_token")
        private val KEY_AUTH_TOKEN = stringPreferencesKey("key_auth_token")
        private val KEY_USER_VERIFIED = intPreferencesKey("key_user_verified")
        private val KEY_NUM_SHIPPED = intPreferencesKey("key_num_shipped")
        private const val STORE_NAME = "tax_data_store"
    }

}

enum class UserStatus(val out: Int) {
    // Offline won't receive offers, Idle will receive offers, Active means that the user is currently in transit.
    OFFLINE(0),
    IDLE(1),
    ACTIVE(2)
}