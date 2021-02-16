package com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMainActivityRepository
import com.lucidsoftworksllc.taxidi.utils.Extensions.getServerResponseInt
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.launch

class DriverMainActivityViewModel (
    private val repository: DriverMainActivityRepository
) : BaseViewModel() {

    fun sendFCMTokenToServer(fcmToken: String) {
        viewModelScope.launch {
            repository.sendFCMTokenToServer(fcmToken).apply {
                when (this) {
                    is Result.Success -> {
                        if (!this.data.error){
                            repository.saveFCMTokenLocally(fcmToken)
                        } else {
                            showSnackBarInt.value = this.data.code.getServerResponseInt()
                        }
                    }
                    is Result.Error -> {
                        showSnackBar.value = this.message
                    }
                    Result.Loading -> {
                    } // Do nothing
                }
            }
        }
    }

}