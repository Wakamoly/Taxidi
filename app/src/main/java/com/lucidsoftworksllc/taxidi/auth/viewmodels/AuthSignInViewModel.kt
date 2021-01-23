package com.lucidsoftworksllc.taxidi.auth.viewmodels

import androidx.lifecycle.MutableLiveData
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseViewModel

class AuthSignInViewModel(
    private val repository: AuthRepository
) : BaseViewModel() {
    // Login credentials
    val signInAs = MutableLiveData<String>()
    val emailAddress = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Register credentials


    fun clearLoading(){
        showLoading.value = false
    }



}