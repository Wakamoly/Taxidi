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
            name = "my_data_store"
        )
    }

    private val username: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USERNAME]
        }

    private val email: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_EMAIL]
        }

    private val fCMToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_FCMTOKEN]
        }

    private val isUserLoggedIn: Flow<Boolean?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_LOGGED_IN]
        }

    suspend fun saveCredentials(username: String, email: String){
        dataStore.edit { preferences ->
            preferences[KEY_EMAIL] = email
            preferences[KEY_USERNAME] = username
            preferences[KEY_USER_LOGGED_IN] = true
        }
        Log.d("UserPreferences", "saveCredentials: isUserLoggedIn -> ${isUserLoggedIn()}")
    }

    suspend fun saveFCMToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_FCMTOKEN] = token
        }
        Log.d("UserPreferences", "saveFCMToken: FCMToken updated -> $fCMToken")
    }

    suspend fun isUserLoggedIn(): Boolean = isUserLoggedIn.first() ?: false

    suspend fun userEmail(): String = email.first().toString()

    suspend fun userUsername(): String = username.first().toString()

    suspend fun fCMToken(): String = fCMToken.first().toString()

    suspend fun clear(){
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val KEY_USERNAME = preferencesKey<String>("key_username")
        private val KEY_EMAIL = preferencesKey<String>("key_email")
        private val KEY_USER_LOGGED_IN = preferencesKey<Boolean>("key_is_user_logged_in")
        private val KEY_FCMTOKEN = preferencesKey<String>("key_fcm_token")
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