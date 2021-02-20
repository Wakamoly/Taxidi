package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.MutableLiveData
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
    val logList = repository.logList
    val newsList = repository.newsList

    val newsNoData = MutableLiveData<Boolean>()
    val logNoData = MutableLiveData<Boolean>()
    val newsLoading = MutableLiveData<Boolean>()
    val logLoading = MutableLiveData<Boolean>()

    init {
        retrieveHomeValues()
        newsLoading.value = true
        logLoading.value = true
    }

    private fun retrieveHomeValues() {
        viewModelScope.launch {
            repository.getUserPrefBits()
            repository.getHomeInfoFromServer()
            newsLoading.value = false
            logLoading.value = false
            newsNoData.value = newsList.value?.isEmpty()
            logNoData.value = logList.value?.isEmpty()
        }
    }

}