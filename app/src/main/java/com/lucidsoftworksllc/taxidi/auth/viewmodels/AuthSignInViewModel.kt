package com.lucidsoftworksllc.taxidi.auth.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.AuthRegisterFragmentDirections
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.others.models.RegisterModel
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

    fun clearLoading(){
        showLoading.value = false
    }

    fun validateRegisterCredentials(){
        if (validateRegisterFirstScreen()){
            navigationCommand.value = NavigationCommand.To(AuthRegisterFragmentDirections.actionAuthRegisterFragmentToAuthRegisterNextStepFragment())
        }
    }

    private fun validateRegisterFirstScreen(): Boolean {
        if (emailAddress.value.isNullOrEmpty() || emailAddress.value?.isEmailValid() == false) {
            showSnackBarInt.value = R.string.err_email_address
            return false
        }

        if (password.value.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_password_empty
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
        // TODO: 1/28/2021 showLoading fadeVisible doesn't seem to update
        showLoading.value = true
        viewModelScope.launch {
            val result = repository.saveUser(registerModel)
            showLoading.value = false
            showSnackBarInt.value = R.string.reminder_saved
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data.
     */
    private fun validateEnteredData(registerModel: RegisterModel): Boolean {

        // Model class values for reference
        /*val authorityType : String,
        val type : String,
        val companyName : String,
        val streetAddress : String,
        val city : String,
        val state : String,
        val zipCode : String,
        val country : String,
        val companyPhone : String,
        val firstName : String,
        val lastName : String,
        val personalPhone : String*/

        if (registerModel.authorityType.isEmpty()) {
            showSnackBarInt.value = R.string.err_select_auth_type
            return false
        }

        if (registerModel.type.isEmpty()) {
            showSnackBarInt.value = R.string.err_select_type
            return false
        }

        if (registerModel.companyName.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_company_name
            return false
        }

        if (registerModel.streetAddress.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_street_address
            return false
        }

        if (registerModel.city.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_city
            return false
        }

        if (registerModel.state.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_state
            return false
        }

        if (registerModel.zipCode.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_zip_code
            return false
        }

        if (registerModel.country.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_country
            return false
        }

        if (registerModel.companyPhone.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_company_phone
            return false
        }

        if (registerModel.firstName.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_firstname
            return false
        }

        if (registerModel.lastName.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_lastname
            return false
        }

        if (registerModel.personalPhone.isEmpty()) {
            showSnackBarInt.value = R.string.err_enter_personal_phone
            return false
        }

        return true
    }

}