package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.base.BaseFragmentNoVM
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.databinding.AuthRegisterFragmentBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import com.lucidsoftworksllc.taxidi.utils.hideKeyboard
import com.lucidsoftworksllc.taxidi.utils.startBaseObservables

class AuthRegisterFragment : BaseFragmentNoVM<AuthRegisterFragmentBinding>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels { ViewModelFactory(AuthRepository(userPreferences)) }

    companion object {
        const val TAG = "AuthRegisterFragment"
    }

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
        /*args.signInAs.let { signInAs ->
            Log.d(TAG, "onViewCreated: SignInAs: $signInAs")
            viewModel.apply {
                //setValues(reminderData)
            }
        }*/
        _viewModel.clearLoading()

        binding.registerFragRegisterButton.setOnClickListener {
            hideKeyboard()
            _viewModel.navigationCommand.value =
                NavigationCommand.To(AuthRegisterFragmentDirections.actionAuthRegisterFragmentToAuthRegisterNextStepFragment())
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AuthRegisterFragmentBinding.inflate(inflater, container, false)

    override fun onStart() {
        super.onStart()
        startBaseObservables(_viewModel)
    }

}