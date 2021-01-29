package com.lucidsoftworksllc.taxidi.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.taxidi.auth.AuthActivity
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import com.lucidsoftworksllc.taxidi.utils.startBaseObservables
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
        userPreferences = UserPreferences(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mCtx = requireContext()
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        return binding.root
    }

    fun logout() = lifecycleScope.launch {
        withContext(Dispatchers.IO) {
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

    override fun onStart() {
        super.onStart()
        startBaseObservables(viewModel)
    }

}