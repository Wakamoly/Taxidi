package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.os.Bundle
import android.view.*
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverHomeFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverHomeRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverHomeAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverHomeViewModel
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.setTitle
import com.lucidsoftworksllc.taxidi.utils.toastShort

class DriverHomeFragment : BaseFragment<DriverHomeViewModel, DriverHomeFragmentBinding, DriverHomeRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_home_title))
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.messaging -> {
                requireContext().toastShort("Not yet implemented")
                // TODO: 2/5/2021 Bring the user to the messaging activity.
            }
            R.id.logout -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.driver_home_menu, menu)
    }

    override fun getViewModel() = DriverHomeViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DriverHomeFragmentBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = DriverHomeRepository(
        userPreferences, // Datastore
        remoteDataSource.buildApi(DriverHomeAPI::class.java, fcmToken)
    )

}