package com.lucidsoftworksllc.taxidi.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel

class AuthActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this).get(AuthSignInViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}