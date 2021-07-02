package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.os.Bundle
import android.view.*
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters.DriverHomeLogListAdapter
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters.DriverHomeNewsListAdapter
import com.lucidsoftworksllc.taxidi.databinding.DriverHomeFragmentBinding
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase
import com.lucidsoftworksllc.taxidi.db.dao.DriverHomeDao
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverHomeRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverHomeAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverHomeViewModel
import com.lucidsoftworksllc.taxidi.utils.Extensions.authToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.setTitle
import com.lucidsoftworksllc.taxidi.utils.Extensions.setup
import com.lucidsoftworksllc.taxidi.utils.Extensions.toastShort

class DriverHomeFragment : BaseFragment<DriverHomeViewModel, DriverHomeFragmentBinding, DriverHomeRepository>() {

    private lateinit var homeViewDao: DriverHomeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewDao = TaxidiDatabase(requireContext()).getDriverHomeDao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_home_title))
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        val logAdapter = DriverHomeLogListAdapter {
            // TODO: 2/19/2021 Navigate accordingly
            when (it.id) {

            }
        }
        binding.driverHomeLogRecycler.setup(logAdapter)

        val newsAdapter = DriverHomeNewsListAdapter {
            // TODO: 2/19/2021 Ellipsize item layout description, provide a layout specifically for this dialog view
            /*val dialogBinding = ItemDriverHomeNewsBinding.inflate(layoutInflater)
            dialogBinding.model = it
            binding.executePendingBindings()
            val dialog = LovelyCustomDialog(requireContext())
            dialog.setView(dialogBinding.root)
                    .setTopColorRes(R.color.primaryColor)
                    .setIcon(R.drawable.ic_baseline_local_shipping_64)
                    .setCancelable(true)
                    .show()*/

            // This works for now, shows the user's profile when clicked
            viewModel.navigationCommand.value =
                    NavigationCommand.To(DriverHomeFragmentDirections.actionDriverHomeFragmentToDriverProfileFragment(userId = it.news_user_id))
        }
        binding.driverHomeNewsRecycler.setup(newsAdapter)
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
            remoteDataSource.buildApi(DriverHomeAPI::class.java, authToken),
            homeViewDao
    )

}