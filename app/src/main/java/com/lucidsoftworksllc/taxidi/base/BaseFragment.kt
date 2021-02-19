package com.lucidsoftworksllc.taxidi.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lucidsoftworksllc.taxidi.auth.AuthActivity
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.base.UserAPI
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.Extensions.deviceUserID
import com.lucidsoftworksllc.taxidi.utils.Extensions.deviceUsername
import com.lucidsoftworksllc.taxidi.utils.Extensions.fcmToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.startBaseObservables
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Base Fragment to observe on the common LiveData objects
 */
abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding, R: BaseRepository> : Fragment() {

    protected lateinit var userPreferences: UserPreferences
    protected lateinit var binding : B
    protected lateinit var viewModel : VM
    protected val remoteDataSource = RemoteDataSource()
    protected lateinit var mCtx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mCtx = requireContext()
        userPreferences = UserPreferences(mCtx)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        startBaseObservables(viewModel)
        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
            // Deactivate FCM token on server
            // TODO: 2/18/2021 Log user out when error code 401 occurs
            val api = remoteDataSource.buildApi(UserAPI::class.java)
            viewModel.logout(api, deviceUsername, deviceUserID, fcmToken)

            userPreferences.clear()
            TaxidiDatabase(mCtx).clearAllTables()
        }
        val intent = Intent(mCtx, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity?.finish()
        startActivity(intent)
    }

    abstract fun getViewModel() : Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : B

    abstract fun getFragmentRepository(): R

}