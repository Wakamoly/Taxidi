package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverNotificationsRepository
import com.lucidsoftworksllc.taxidi.utils.Extensions.getServerResponseInt
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.launch

class DriverNotificationsViewModel (
    private val repository: DriverNotificationsRepository
) : BaseViewModel(repository) {

    val notifications = repository.notifications

    init {
        showLoading.value = true
        getNotifications()
    }

    private fun getNotifications() {
        viewModelScope.launch {
            showNoData.value = notifications.value?.isEmpty()
            repository.refreshNotifications().apply {
                when (this) {
                    is Result.Error -> { showSnackBarInt.value = R.string.srverr_unknown }
                    is Result.Success -> {
                        if (this.data.error) {
                            showSnackBarInt.value = this.data.code.getServerResponseInt()
                        }
                    }
                    else -> {} // Unused
                }
            }
            showLoading.value = false
            showNoData.value = notifications.value?.isEmpty()
        }
    }

    fun setAllNotificationsRead() {
        viewModelScope.launch {
            repository.setAllNotificationsRead()
        }
    }

    fun setSingleNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.setSingleNotificationRead(id)
        }
    }

}