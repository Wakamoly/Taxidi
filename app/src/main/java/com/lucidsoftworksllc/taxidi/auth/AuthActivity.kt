package com.lucidsoftworksllc.taxidi.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthActivity : AppCompatActivity() {

    private lateinit var viewModel : AuthSignInViewModel
    private lateinit var fcmToken: String
    private lateinit var signedInAs: String
    private var isUserLoggedIn: Boolean = false
    private lateinit var userPreferences : UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences(this@AuthActivity)

        fcmToken = getFcmToken
        isUserLoggedIn = getIsUserLoggedIn
        signedInAs = getSignedInAs

        val factory = ViewModelFactory(AuthRepository(userPreferences, RemoteDataSource().buildApi(RegisterAPI::class.java, fcmToken)))
        viewModel = ViewModelProvider(this, factory).get(AuthSignInViewModel::class.java)

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    val fbToken = task.result!!
                    Log.d("Installations", "Installation auth token: $fbToken, current -> $fcmToken")
                    if (fbToken != fcmToken) {
                        userPreferences.saveFCMToken(fbToken)
                    }
                }
            } else {
                Log.e("Installations", "Unable to get Installation auth token")
            }
        }

        if (isUserLoggedIn){
            signUserIn()
        } else {
            setContentView(R.layout.activity_auth)
            setupObservers()
        }

    }

    private fun signUserIn() {
        lifecycleScope.launch {
            fcmToken = getFcmToken
            isUserLoggedIn = getIsUserLoggedIn
            signedInAs = getSignedInAs
            if (signedInAs == "driver") {
                finish()
                startNewActivity(DriverMainActivity::class.java)
            } else {
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