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
    private val dataStore: DataStore<Preferences>

    init {
        dataStore = applicationContext.createDataStore(
            name = STORE_NAME
        )
    }

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

    private val fCMToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_FCMTOKEN]
        }

    private val isUserLoggedIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_LOGGED_IN]
        }

    suspend fun saveCredentials(username: String, userID: Int, type: String){
        dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
            preferences[KEY_USER_ID] = userID
            preferences[KEY_USER_TYPE] = type
            preferences[KEY_USER_LOGGED_IN] = true
        }
        Log.d("UserPreferences", "saveCredentials: isUserLoggedIn -> ${isUserLoggedIn()}")
        Log.d("UserPreferences", "saveCredentials: userID -> ${userID()}")
        Log.d("UserPreferences", "saveCredentials: userUsername -> ${userUsername()}")
        Log.d("UserPreferences", "saveCredentials: userType -> ${userType()}")
    }

    suspend fun saveFCMToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_FCMTOKEN] = token
        }
        Log.d("UserPreferences", "saveFCMToken: FCMToken updated -> ${fCMToken()})")
    }

    suspend fun isUserLoggedIn(): Boolean = isUserLoggedIn.first() ?: false

    suspend fun userID(): String = userID.first().toString()

    suspend fun userUsername(): String = username.first().toString()

    suspend fun userType(): String = userType.first().toString()

    suspend fun fCMToken(): String = fCMToken.first().toString()

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_USERNAME = stringPreferencesKey("key_username")
        private val KEY_USER_ID = intPreferencesKey("key_user_id")
        private val KEY_USER_TYPE = stringPreferencesKey("key_user_type")
        private val KEY_USER_LOGGED_IN = booleanPreferencesKey("key_is_user_logged_in")
        private val KEY_FCMTOKEN = stringPreferencesKey("key_fcm_token")
        private const val STORE_NAME = "tax_data_store"
    }

}

/**
 * Extension functions for DataStore
 */
fun <T> DataStore<Preferences>.getValueFlow(
        key: Preferences.Key<T>,
        defaultValue: T
): Flow<T> {
    return this.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                preferences[key] ?: defaultValue
            }
}

suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    this.edit { preferences ->
        preferences[key] = value
    }
}