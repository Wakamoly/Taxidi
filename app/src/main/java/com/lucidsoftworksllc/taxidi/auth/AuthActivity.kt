package com.lucidsoftworksllc.taxidi.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api.RegisterAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.DriverMainActivity
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.*
import com.lucidsoftworksllc.taxidi.utils.Extensions.getSignedInAs
import com.lucidsoftworksllc.taxidi.utils.Extensions.startNewActivity
import com.lucidsoftworksllc.taxidi.utils.Extensions.toastLong
import kotlinx.coroutines.*

class AuthActivity : AppCompatActivity() {

    private lateinit var userPreferences : UserPreferences

    private val viewModel : AuthSignInViewModel by viewModels {
        ViewModelFactory(AuthRepository(UserPreferences(this@AuthActivity), RemoteDataSource().buildApi(RegisterAPI::class.java)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            userPreferences = UserPreferences(this@AuthActivity)
            if (userPreferences.isUserLoggedIn()){
                signUserIn()
            } else {
                setContentView(R.layout.activity_auth)
                setupObservers()
            }
        }
    }

    private fun signUserIn() {
        lifecycleScope.launch {
            // TODO: 2/16/2021 Fix this, shows toast as well as logs the user in
            // TODO: 2/16/2021 Fixed?
            val userType = getSignedInAs
            if (userType == "driver") {
                finish()
                startNewActivity(DriverMainActivity::class.java)
            } else if (userType == "company") {
                toastLong("Login successful! This would normally move the user to a main activity, but business accounts don't work just yet... Clearing UserPreferences to return to login screen, please log in as a driver.")
                userPreferences.clear()
                startNewActivity(AuthActivity::class.java)
            }
        }
    }

    private fun setupObservers() {
        viewModel.signInRequestSuccess.observe(this, {
            when (it) {
                true -> {
                    signUserIn()
                }
            }
        })
    }

}