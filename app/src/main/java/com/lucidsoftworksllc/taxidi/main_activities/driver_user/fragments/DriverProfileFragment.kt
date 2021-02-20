package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.navArgs
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverProfileFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverProfileRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverProfileAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverProfileViewModel
import com.lucidsoftworksllc.taxidi.utils.Extensions.authToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.deviceUserID
import com.lucidsoftworksllc.taxidi.utils.Extensions.fcmToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.setTitle
import com.lucidsoftworksllc.taxidi.utils.Extensions.toastShort

class DriverProfileFragment : BaseFragment<DriverProfileViewModel, DriverProfileFragmentBinding, DriverProfileRepository>() {

    private val args: DriverProfileFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_profile_title))
        initObservers()
    }

    private fun initObservers() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        when {
            args.userId != 0 -> {
                viewModel.loadProfile(args.userId)
            }
            args.username != null -> {
                viewModel.getProfileID(args.username!!)
            }
            else -> {
                // No user ID or username to load, load our profile
                viewModel.loadProfile(deviceUserID)
            }
        }
        viewModel.profileLoaded.observe(viewLifecycleOwner, {
            when (it) {
                true -> {
                    binding.driverProfileMotionLayout.transitionToEnd()
                }
                false -> {
                    // Do nothing
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_my_profile -> {
                viewModel.showSnackBarInt.value = R.string.not_implemented
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
        remoteDataSource.buildApi(DriverProfileAPI::class.java, authToken)
    )
    override fun getViewModel() = DriverProfileViewModel::class.java

}