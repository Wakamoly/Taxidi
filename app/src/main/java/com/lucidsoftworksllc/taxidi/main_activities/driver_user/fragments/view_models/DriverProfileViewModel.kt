package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.BuildConfig
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverProfileRepository
import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverProfileResponseModel
import com.lucidsoftworksllc.taxidi.utils.Result
import com.lucidsoftworksllc.taxidi.utils.getServerResponseInt
import kotlinx.coroutines.launch

class DriverProfileViewModel (
    private val repository: DriverProfileRepository
) : BaseViewModel() {

    val profileDisplayName = MutableLiveData<String>()
    val profileBackImage = MutableLiveData<String>()
    val profileImage = MutableLiveData<String>()
    val profileRating = MutableLiveData<Float>()
    val profileCurrentStatus = MutableLiveData<Int>()

    val profileDescription = MutableLiveData<String>()
    val profileType = MutableLiveData<String>()
    val profileClosed = MutableLiveData<Int>()
    val profileBanned = MutableLiveData<Int>()
    val profileVerified = MutableLiveData<Int>()
    val profileLastOnline = MutableLiveData<String>()
    val profileNumShipped = MutableLiveData<Int>()

    // TODO: 2/3/2021 Need for a reset function?

    fun getProfileID(username: String) {
        showLoading.value = true
        viewModelScope.launch {
            when (val result = repository.getProfileID(username)) {
                is Result.Success -> { submitProfileFromServer(result.data) }
                is Result.Error -> {
                    showLoading.value = false
                    showSnackBar.value = result.message
                }
                Result.Loading -> showLoading.value = true
            }
        }
    }

    fun loadProfile(userId: Int) {
        showLoading.value = true
        viewModelScope.launch {
            when (val result = repository.loadProfile(userId)) {
                is Result.Success -> { submitProfileFromServer(result.data) }
                is Result.Error -> {
                    showLoading.value = false
                    showSnackBar.value = result.message
                }
                Result.Loading -> showLoading.value = true
            }
        }
    }

    private fun submitProfileFromServer(data: DriverProfileResponseModel) {
        showLoading.value = false
        if (!data.error) {
            if (BuildConfig.DEBUG) {
                showSnackBarInt.value = data.code.getServerResponseInt()
            }
            data.result?.apply {
                profileDisplayName.value = display_name
                profileBackImage.value = back_pic
                profileImage.value = profile_pic
                profileRating.value = average.toFloat()
                profileCurrentStatus.value = status
                profileDescription.value = description
                profileType.value = type
                profileClosed.value = user_closed
                profileBanned.value = user_banned
                profileVerified.value = verified
                profileLastOnline.value = last_online
                profileNumShipped.value = num_shipped
            }
        } else {
            showSnackBarInt.value = data.code.getServerResponseInt()
        }
    }

}