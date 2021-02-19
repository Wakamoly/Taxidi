package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverHomeRepository
import kotlinx.coroutines.launch

class DriverHomeViewModel(
    private val repository: DriverHomeRepository
) : BaseViewModel(repository) {

    val userStatus = repository.userStatus
    val numShipped = repository.numShipped
    val userVerified = repository.userVerified

    init {
        retrieveHomeValues()
    }

    private fun retrieveHomeValues() {
        viewModelScope.launch {
            repository.getUserPrefBits()
        }
    }

}