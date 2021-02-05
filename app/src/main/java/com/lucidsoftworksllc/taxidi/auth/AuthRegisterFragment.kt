package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api.RegisterAPI
import com.lucidsoftworksllc.taxidi.base.BaseFragmentNoVM
import com.lucidsoftworksllc.taxidi.databinding.AuthRegisterFragmentBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.startBaseObservables

class AuthRegisterFragment : BaseFragmentNoVM<AuthRegisterFragmentBinding>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels()

    companion object {
        const val TAG = "AuthRegisterFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
    }

    private fun initView(){
        _viewModel.clearLoading()
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