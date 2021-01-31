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

    //val viewModel by lazy { ViewModelProvider(this).get(AuthSignInViewModel::class.java) }
    private lateinit var viewModel : AuthSignInViewModel
    private lateinit var fcmToken: String
    private lateinit var signedInAs: String
    private var isUserLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this@AuthActivity)

        runBlocking {
            fcmToken = userPreferences.fCMToken()
            isUserLoggedIn = userPreferences.isUserLoggedIn()
            signedInAs = userPreferences.userType()
        }

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
                        // TODO: 1/28/2021 Also send to the server
                    }
                }
            } else {
                Log.e("Installations", "Unable to get Installation auth token")
            }
        }

        if (isUserLoggedIn){
            if (signedInAs == "driver") {
                finish()
                startNewActivity(DriverMainActivity::class.java)
            } else {
                toastLong("Login successful! This would normally move the user to a main activity. Clearing UserPreferences to return to login screen.")
                runBlocking { userPreferences.clear() }
                startNewActivity(AuthActivity::class.java)
            }
        } else {
            setContentView(R.layout.activity_auth)
            setupObservers()
        }

    }

    private fun setupObservers() {

        viewModel.signInRequestSuccess.observe(this, {
            when (it) {
                true -> {
                    // TODO: 1/28/2021 Navigate to which-ever Main Activity per the user's type
                    // TODO: 1/30/2021 Move to Main Activities
                    toastLong("Login successful! This would normally move the user to a main activity")
                }
            }
        })

    }

}