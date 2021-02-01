package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.auth.AuthActivity
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.setTitle
import com.lucidsoftworksllc.taxidi.utils.toastShort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DriverHomeFragment : Fragment() {

    private lateinit var viewModel: DriverHomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        setTitle(getString(R.string.driver_fragment_home_title))
        return inflater.inflate(R.layout.driver_home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DriverHomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.messaging -> {
                requireContext().toastShort("Not yet implemented")
            }
            R.id.logout -> {
                logout()
            }
        }
        return super.onOptionsItemSelected(item)

    }

    // TODO: 2/1/2021 Remove this function after BaseFragment implementation
    private fun logout() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            UserPreferences(requireContext()).clear()
            //TaxidiDatabase(requireContext()).clearAllTables()
        }
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.finish()
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.driver_home_menu, menu)
    }

}