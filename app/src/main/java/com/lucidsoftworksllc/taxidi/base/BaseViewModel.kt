package com.lucidsoftworksllc.taxidi.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.base.UserAPI
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMainAPI
import com.lucidsoftworksllc.taxidi.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base class for View Models to declare the common LiveData objects in one place
 */
abstract class BaseViewModel(
        private val repository: BaseRepository
) : ViewModel() {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()
    val showErrorMessage: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBar: SingleLiveEvent<String> = SingleLiveEvent()
    val showSnackBarInt: SingleLiveEvent<Int> = SingleLiveEvent()
    val showToast: SingleLiveEvent<String> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showNoData: MutableLiveData<Boolean> = MutableLiveData()

    suspend fun logout(api: UserAPI, username: String, userID: Int, fcmToken: String) = withContext(Dispatchers.IO) {
        repository.logout(api, username, userID, fcmToken)
    }

}