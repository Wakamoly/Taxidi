package com.lucidsoftworksllc.taxidi.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMainActivityRepository
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMainActivityViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverHomeViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverHomeRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverNotificationsRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverProfileRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMapViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverNotificationsViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverProfileViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            // Auth Activity
            modelClass.isAssignableFrom(AuthSignInViewModel::class.java) -> AuthSignInViewModel(repository as AuthRepository) as T

            //Driver Activity
            modelClass.isAssignableFrom(DriverMainActivityViewModel::class.java) -> DriverMainActivityViewModel(repository as DriverMainActivityRepository) as T
            modelClass.isAssignableFrom(DriverHomeViewModel::class.java) -> DriverHomeViewModel(repository as DriverHomeRepository) as T
            modelClass.isAssignableFrom(DriverMapViewModel::class.java) -> DriverMapViewModel(repository as DriverMapRepository) as T
            modelClass.isAssignableFrom(DriverNotificationsViewModel::class.java) -> DriverNotificationsViewModel(repository as DriverNotificationsRepository) as T
            modelClass.isAssignableFrom(DriverProfileViewModel::class.java) -> DriverProfileViewModel(repository as DriverProfileRepository) as T

            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}