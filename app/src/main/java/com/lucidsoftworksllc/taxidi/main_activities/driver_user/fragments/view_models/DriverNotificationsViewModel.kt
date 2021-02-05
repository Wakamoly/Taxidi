package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverNotificationsRepository
import com.lucidsoftworksllc.taxidi.utils.Result
import com.lucidsoftworksllc.taxidi.utils.getServerResponseInt
import kotlinx.coroutines.launch

class DriverNotificationsViewModel (
    private val repository: DriverNotificationsRepository
) : BaseViewModel() {

    val notifications = repository.notifications

    init {
        viewModelScope.launch {
            if (notifications.value?.isEmpty() != false){
                showLoading.value = true
            }
            repository.refreshNotifications().apply {
                showLoading.value = false
                if (this is Result.Success){
                    if (this.data.error) {
                        showSnackBarInt.value = this.data.code.getServerResponseInt()
                    }
                    if (notifications.value?.isEmpty() != false){
                        showNoData.value = true
                    }
                } else {
                    showNoData.value = true
                    showSnackBarInt.value = R.string.srverr_notification_generic
                }
            }

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