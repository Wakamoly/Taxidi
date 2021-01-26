package com.lucidsoftworksllc.taxidi.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel

class AuthActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(AuthSignInViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fbToken = task.result!!
                Log.d("Installations", "Installation auth token: $fbToken")

                // TODO Check match to Datastore FCMToken and save.
                /*if (fbToken != SharedPrefManager.getInstance(applicationContext)!!.fCMToken) {
                    SharedPrefManager.getInstance(applicationContext)!!.updateToken(fbToken)
                }*/
            } else {
                Log.e("Installations", "Unable to get Installation auth token")
            }
        }

        // TODO: 1/25/2021 If user is logged in, take them to the main activity. Else:
        setContentView(R.layout.activity_auth)

    }
}