package com.lucidsoftworksllc.taxidi.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.viewmodels.AuthSignInViewModel
import com.lucidsoftworksllc.taxidi.base.BaseFragmentNoVM
import com.lucidsoftworksllc.taxidi.databinding.FragmentAuthRegisterNextStepBinding
import com.lucidsoftworksllc.taxidi.others.models.RegisterModel
import com.lucidsoftworksllc.taxidi.utils.Extensions.startBaseObservables


class AuthRegisterNextStepFragment : BaseFragmentNoVM<FragmentAuthRegisterNextStepBinding>() {

    private val _viewModel: AuthSignInViewModel by activityViewModels()

    companion object {
        const val TAG = "AuthRegNextStepFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel
    }

    private fun initView(){
        _viewModel.clearLoading()

        val authTypeItems = listOf(getString(R.string.register_authority_type_1), getString(R.string.register_authority_type_2), getString(R.string.register_authority_type_3))
        val authTypeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, authTypeItems)
        val dropdown = binding.authRegister2Autocomplete
        dropdown.setAdapter(authTypeAdapter)
        dropdown.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selected = authTypeAdapter.getItem(position).toString()
            _viewModel.authorityType.value = selected
            Log.d(TAG, "onItemSelected: authorityType selected! $selected")
        }
        dropdown.id = binding.authRegister2Autocomplete.id

        val driverTypeItems = listOf(getString(R.string.register_driver_type_1), getString(R.string.register_driver_type_2), getString(R.string.register_driver_type_3), getString(R.string.register_driver_type_4), getString(R.string.register_driver_type_5))
        val driverTypeAdapter = ArrayAdapter(requireContext(), R.layout.list_item, driverTypeItems)
        val dropdown2 = binding.authRegister2AutocompleteType
        dropdown2.setAdapter(driverTypeAdapter)
        dropdown2.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selected = driverTypeAdapter.getItem(position).toString()
            _viewModel.type.value = selected
            Log.d(TAG, "onItemSelected: type selected! $selected")
        }

        binding.registerFragRegisterButton.setOnClickListener {
            setupRegister()
        }
    }

    private fun setupRegister() {
        val registerModel = RegisterModel(
                _viewModel.signInAs.value,
                _viewModel.username.value,
                _viewModel.emailAddress.value,
                _viewModel.password.value,
                _viewModel.authorityType.value,
                _viewModel.type.value,
                _viewModel.companyName.value,
                _viewModel.streetAddress.value,
                _viewModel.city.value,
                _viewModel.state.value,
                _viewModel.zipCode.value,
                _viewModel.country.value,
                _viewModel.companyPhone.value,
                _viewModel.firstName.value,
                _viewModel.lastName.value,
                _viewModel.personalPhone.value
        )

        _viewModel.register(registerModel)
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