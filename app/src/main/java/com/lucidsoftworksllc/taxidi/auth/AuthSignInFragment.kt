package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api.RegisterAPI
import com.lucidsoftworksllc.taxidi.base.BaseFragmentNoVM
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.databinding.AuthSignInFragmentBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.hideKeyboard
import com.lucidsoftworksllc.taxidi.utils.startBaseObservables

class AuthSignInFragment : BaseFragmentNoVM<AuthSignInFragmentBinding>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels { ViewModelFactory(
            AuthRepository(userPreferences, remoteDataSource.buildApi(RegisterAPI::class.java, fcmToken))
    ) }

    companion object {
        const val TAG = "AuthSignInFragment"
    }

    private val args: AuthSignInFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
    }

    private fun initView(){
        args.signInAs.let { signInAs ->
            Log.d(TAG, "onViewCreated: SignInAs: $signInAs")
            _viewModel.apply {
                _viewModel.signInAs.value = signInAs
            }
        }
        _viewModel.clearLoading()

        binding.signinFragLoginButton.setOnClickListener {
            hideKeyboard()
            _viewModel.logUserIn()
        }

        binding.signinFragRegisterButton.setOnClickListener {
            hideKeyboard()
            _viewModel.navigationCommand.value =
                NavigationCommand.To(AuthSignInFragmentDirections.actionAuthSignInFragmentToAuthRegisterFragment())
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AuthSignInFragmentBinding.inflate(inflater, container, false)

    override fun onStart() {
        super.onStart()
        startBaseObservables(_viewModel)
    }

}