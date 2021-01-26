package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.AuthRepository
import com.lucidsoftworksllc.taxidi.base.BaseFragmentNoVM
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.databinding.AuthRegisterFragmentBinding
import com.lucidsoftworksllc.taxidi.databinding.FragmentAuthRegisterNextStepBinding
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import com.lucidsoftworksllc.taxidi.utils.hideKeyboard
import com.lucidsoftworksllc.taxidi.utils.startBaseObservables

class AuthRegisterNextStepFragment : BaseFragmentNoVM<FragmentAuthRegisterNextStepBinding>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels { ViewModelFactory(
        AuthRepository(userPreferences)
    ) }

    companion object {
        const val TAG = "AuthRegisterNextStepFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.viewModel = _viewModel

        // binding.loginButton.setOnClickListener {
        //hideKeyboard()
    // launchSignInFlow()
    // }

    }

    private fun initView(){
        /*args.signInAs.let { signInAs ->
            Log.d(TAG, "onViewCreated: SignInAs: $signInAs")
            viewModel.apply {
                //setValues(reminderData)
            }
        }*/
        _viewModel.clearLoading()

        val authTypeItems = listOf(getString(R.string.register_authority_type_1), getString(R.string.register_authority_type_2), getString(R.string.register_authority_type_3))
        val authTypAdapter = ArrayAdapter(requireContext(), R.layout.list_item, authTypeItems)
        (binding.authRegister2AuthorityTypeDropdown.editText as? AutoCompleteTextView)?.setAdapter(authTypAdapter)

        val driverTypeItems = listOf(getString(R.string.register_driver_type_1), getString(R.string.register_driver_type_2), getString(R.string.register_driver_type_3), getString(R.string.register_driver_type_4), getString(R.string.register_driver_type_5))
        val driverTypeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, driverTypeItems)
        (binding.authRegister2TypeDropdown.editText as? AutoCompleteTextView)?.setAdapter(driverTypeAdapter)

    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentAuthRegisterNextStepBinding.inflate(inflater, container, false)

    override fun onStart() {
        super.onStart()
        startBaseObservables(_viewModel)
    }

}