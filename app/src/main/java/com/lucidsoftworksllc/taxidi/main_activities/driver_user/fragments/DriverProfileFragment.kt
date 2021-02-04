package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.os.Bundle
import android.view.*
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverProfileFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverProfileRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverProfileAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverProfileViewModel
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.setTitle
import com.lucidsoftworksllc.taxidi.utils.toastShort

class DriverProfileFragment : BaseFragment<DriverProfileViewModel, DriverProfileFragmentBinding, DriverProfileRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_profile_title))
        binding.viewModel = viewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_my_profile -> {
                requireContext().toastShort("Not yet implemented")
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.driver_profile_menu, menu)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DriverProfileFragmentBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = DriverProfileRepository(
        userPreferences,
        remoteDataSource.buildApi(DriverProfileAPI::class.java, fcmToken)
    )
    override fun getViewModel() = DriverProfileViewModel::class.java

}