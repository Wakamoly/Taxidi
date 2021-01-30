package com.lucidsoftworksllc.taxidi.auth.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.multidex.BuildConfig
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.AuthRegisterFragmentDirections
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.others.models.RegisterModel
import com.lucidsoftworksllc.taxidi.utils.Result
import com.lucidsoftworksllc.taxidi.utils.getServerResponseInt
import com.lucidsoftworksllc.taxidi.utils.isEmailValid
import kotlinx.coroutines.launch

class AuthSignInViewModel(
    private val repository: AuthRepository
) : BaseViewModel() {
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
                    // TODO: 1/28/2021 if response error == true, decode the string response (1xxx) with String.getServerResponseInt(): Int extension function
                    if (result.data.error == false) {
                        Log.d("AuthSignInVM", "saveUser: YAY")
                        /*showSnackBarInt.value = R.string.reminder_saved
                        navigationCommand.value = NavigationCommand.Back*/
                        // TODO: 1/28/2021 Log the user in
                        // TODO: 1/28/2021 Save credentials to DataStore
                        // TODO: 1/28/2021 Navigate to which-ever Main Activity per the user's type
                    } else {
                        showSnackBarInt.value = result.data.code?.getServerResponseInt()
                    }
                    if (BuildConfig.DEBUG) {
                        // TODO: 1/28/2021 show snackbar for response in data class
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