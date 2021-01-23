package com.lucidsoftworksllc.taxidi.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.databinding.AuthSignInFragmentBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory

class AuthSignInFragment : BaseFragment<AuthSignInViewModel, AuthSignInFragmentBinding, AuthRepository>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels { ViewModelFactory(getFragmentRepository()) }

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
        binding.viewModel = _viewModel
       // binding.loginButton.setOnClickListener { launchSignInFlow() }
    }

    private fun initView(){
        args.signInAs.let { signInAs ->
            Log.d(TAG, "onViewCreated: SignInAs: $signInAs")
            _viewModel.apply {
                _viewModel.signInAs.value = signInAs
            }
        }
        _viewModel.clearLoading()

        binding.signinFragRegisterButton.setOnClickListener {
            viewModel.navigationCommand.value =
                NavigationCommand.To(AuthSignInFragmentDirections.actionAuthSignInFragmentToAuthRegisterFragment())
        }
    }

    override fun getViewModel() = AuthSignInViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AuthSignInFragmentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository(userPreferences)

}