package com.lucidsoftworksllc.taxidi.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.AuthSignInFragmentBinding

class AuthSignInFragment : BaseFragment<AuthSignInViewModel, AuthSignInFragmentBinding, AuthRepository>() {

    companion object {
        const val TAG = "AuthSignInFragment"
    }

    private val args: AuthSignInFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
       // binding.loginButton.setOnClickListener { launchSignInFlow() }
    }

    private fun initView(){
        args.signInAs.let { signInAs ->
            Log.d(TAG, "onViewCreated: SignInAs: $signInAs")
            viewModel.apply {
                //setValues(reminderData)
            }
        }
        //viewModel.clearLoading()
    }

    override fun getViewModel() = AuthSignInViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AuthSignInFragmentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository(userPreferences)

}