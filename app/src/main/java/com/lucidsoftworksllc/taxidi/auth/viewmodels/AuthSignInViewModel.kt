package com.lucidsoftworksllc.taxidi.auth.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.BuildConfig
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.AuthRegisterFragmentDirections
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.others.models.RegisterModel
import com.lucidsoftworksllc.taxidi.utils.Extensions.getServerResponseInt
import com.lucidsoftworksllc.taxidi.utils.Extensions.isEmailValid
import com.lucidsoftworksllc.taxidi.utils.Result
import kotlinx.coroutines.launch

class AuthSignInViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {
    // Login credentials
    val signInAs = MutableLiveData<String>()
    val emailAddress = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    // Register credentials
    val username = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()

    // Register Step 2 credentials
    val authorityType = MutableLiveData<String>()
    val type = MutableLiveData<String>()
    val companyName = MutableLiveData<String>()
    val streetAddress = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zipCode = MutableLiveData<String>()
    val country = MutableLiveData<String>()
    val companyPhone = MutableLiveData<String>()
    val firstName = MutableLiveData<String>()
    val lastName = MutableLiveData<String>()
    val personalPhone = MutableLiveData<String>()

    val registerShowLoading = MutableLiveData<Boolean>()

    // Observable for AuthActivity to move to the corresponding main activity
    val signInRequestSuccess = MutableLiveData<Boolean>()

    init {
        signInRequestSuccess.value = false
        registerShowLoading.value = false
        showLoading.value = false
    }

    fun clearLoading(){
        showLoading.value = false
    }

    fun validateRegisterCredentials(){
        if (validateRegisterFirstScreen()){
            navigationCommand.value = NavigationCommand.To(AuthRegisterFragmentDirections.actionAuthRegisterFragmentToAuthRegisterNextStepFragment())
        }
    }

    private fun validateRegisterFirstScreen(): Boolean {
        if (username.value.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_username_empty
            return false
        } else if (username.value!!.length < 4 || username.value!!.length > 20){
            showSnackBarInt.value = R.string.err_username_value_incorrect
            return false
        }

        if (!validateUserLoginCredentials()){
            // Reusing code for email and pass
            return false
        }

        if (password2.value.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_password_empty
            return false
        }

        if (password.value != password2.value){
            showSnackBarInt.value = R.string.err_password_not_match
            return false
        }

        return true
    }

    /**
     * Validate the entered data then save the reminder data to the DataSource.
     */
    fun register(registerModel: RegisterModel){
        Log.d(AuthSignInViewModel::class.java.simpleName, "register: register button pressed!")
        if (validateEnteredData(registerModel)) {
            saveUser(registerModel)
        }
    }

    /**
     * Save the user to the data source.
     */
    private fun saveUser(registerModel: RegisterModel) {
        // TODO: 1/28/2021 showLoading -> fadeVisible doesn't seem to update
        registerShowLoading.value = true
        viewModelScope.launch {
            when (val result = repository.saveUser(registerModel)){

                is Result.Success -> {
                    registerShowLoading.value = false
                    // 1/28/2021 -> if response error == true, decode the string response (1xxx) with String.getServerResponseInt(): Int extension function
                    if (result.data.error == false) {
                        Log.d("AuthSignInVM", "saveUser: YAY")
                        if (BuildConfig.DEBUG) {
                            showSnackBarInt.value = result.data.code?.getServerResponseInt()
                        }
                        // 1/28/2021 Log the user in
                        logUserIn()
                    } else {
                        showSnackBarInt.value = result.data.code?.getServerResponseInt()
                    }

                }

                is Result.Error -> {
                    showLoading.value = false
                    showSnackBar.value = result.message
                }

                Result.Loading -> registerShowLoading.value = true

            }
        }
    }

    fun logUserIn() {
        if (validateUserLoginCredentials()){
            showLoading.value = true
            viewModelScope.launch {
                when (val result = repository.login(emailAddress.value.toString(), password.value.toString())){

                    is Result.Success -> {
                        registerShowLoading.value = false
                        showLoading.value = false
                        // 1/28/2021 -> if response error == true, decode the string response (1xxx) with String.getServerResponseInt(): Int extension function
                        if (!result.data.error) {
                            Log.d("AuthSignInVM", "logUserIn: YAY")
                            if (BuildConfig.DEBUG) {
                                showSnackBarInt.value = result.data.code.getServerResponseInt()
                            }
                            val username = result.data.result?.username
                            val userID = result.data.result?.user_id
                            val type = result.data.result?.type
                            val authToken = result.data.result?.auth_token
                            if (username != null && userID != null && type != null && authToken != null){
                                repository.saveCredentials(username, userID, type, authToken)
                                signInRequestSuccess.value = true
                            }
                        } else {
                            showSnackBarInt.value = result.data.code.getServerResponseInt()
                        }

                    }

                    is Result.Error -> {
                        registerShowLoading.value = false
                        showLoading.value = false
                        showSnackBar.value = result.message
                    }

                    Result.Loading -> {
                        showLoading.value = true
                        registerShowLoading.value = true
                    }

                }
            }
        }
    }

    private fun validateUserLoginCredentials(): Boolean {
        if (emailAddress.value.isNullOrEmpty() || emailAddress.value?.isEmailValid() == false) {
            showSnackBarInt.value = R.string.err_email_address
            return false
        }

        if (password.value.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_password_empty
            return false
        } else if (password.value!!.length < 4) {
            showSnackBarInt.value = R.string.err_password_invalid
            return false
        }

        return true
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data.
     */
    private fun validateEnteredData(registerModel: RegisterModel): Boolean {

        if (registerModel.username.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_username_empty
            return false
        } else if (registerModel.username.length < 4 || username.value!!.length > 20){
            showSnackBarInt.value = R.string.err_username_value_incorrect
            return false
        }

        if (registerModel.emailAddress.isNullOrEmpty() || !registerModel.emailAddress.isEmailValid()) {
            showSnackBarInt.value = R.string.err_email_address
            return false
        }

        if (registerModel.password.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_password_empty
            return false
        } else if (registerModel.password.length < 4) {
            showSnackBarInt.value = R.string.err_password_invalid
            return false
        }

        if (registerModel.authorityType.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_auth_type
            return false
        }

        if (registerModel.type.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_select_type
            return false
        }

        if (registerModel.companyName.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_company_name
            return false
        }

        if (registerModel.streetAddress.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_street_address
            return false
        }

        if (registerModel.city.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_city
            return false
        }

        if (registerModel.state.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_state
            return false
        }

        if (registerModel.zipCode.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_zip_code
            return false
        }

        if (registerModel.country.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_country
            return false
        }

        if (registerModel.companyPhone.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_company_phone
            return false
        }

        if (registerModel.firstName.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_firstname
            return false
        }

        if (registerModel.lastName.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_lastname
            return false
        }

        if (registerModel.personalPhone.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_personal_phone
            return false
        }

        return true
    }

}