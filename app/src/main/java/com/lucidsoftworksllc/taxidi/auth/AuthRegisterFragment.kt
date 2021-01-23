package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.AuthRegisterFragmentBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory

class AuthRegisterFragment : BaseFragment<AuthSignInViewModel, AuthRegisterFragmentBinding, AuthRepository>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels { ViewModelFactory(getFragmentRepository()) }

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
    }

    override fun getViewModel() = AuthSignInViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = AuthRegisterFragmentBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository(userPreferences)

}