package com.lucidsoftworksllc.taxidi.utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthSignInViewModel::class.java) -> AuthSignInViewModel(repository as AuthRepository) as T
            /*modelClass.isAssignableFrom(RemindersListViewModel::class.java) -> RemindersListViewModel(repository as RemindersLocalRepository) as T
            modelClass.isAssignableFrom(SaveReminderViewModel::class.java) -> SaveReminderViewModel(
                Application(), repository as RemindersLocalRepository
            ) as T*/
            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}