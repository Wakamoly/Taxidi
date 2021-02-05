package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.os.Bundle
import android.view.*
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverNotificationsFragmentBinding
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase
import com.lucidsoftworksllc.taxidi.db.dao.DriverNotificationDao
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverNotificationsRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverNotificationsAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverNotificationsViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters.DriverNotificationsListAdapter
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.setTitle
import com.lucidsoftworksllc.taxidi.utils.setup
import com.lucidsoftworksllc.taxidi.utils.toastShort

class DriverNotificationsFragment : BaseFragment<DriverNotificationsViewModel, DriverNotificationsFragmentBinding, DriverNotificationsRepository>() {

    private lateinit var notificationDao: DriverNotificationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationDao = TaxidiDatabase(requireContext()).getDriverNotiDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_notifications_title))
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = DriverNotificationsListAdapter {
            // TODO: 2/5/2021 Navigate accordingly
            when (it.link) {

            }
            viewModel.setSingleNotificationRead(it.id)
            //navigateToAddReminder(it)
        }

        // Setup the recycler view using the extension function.
        binding.driverNotificationsRecycler.setup(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.read_all -> {
                viewModel.setAllNotificationsRead()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.driver_notifications_menu, menu)
    }

    override fun getViewModel() = DriverNotificationsViewModel::class.java
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DriverNotificationsFragmentBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = DriverNotificationsRepository(
        userPreferences,
        remoteDataSource.buildApi(DriverNotificationsAPI::class.java, fcmToken),
        notificationDao
    )

}