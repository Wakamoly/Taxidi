package com.lucidsoftworksllc.taxidi.main_activities.driver_user

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMainActivityRepository
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMainAPI
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMainActivityViewModel
import com.lucidsoftworksllc.taxidi.databinding.ActivityDriverMainBinding
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.Extensions.getAuthToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.getFcmToken
import com.lucidsoftworksllc.taxidi.utils.Extensions.snackbar
import com.lucidsoftworksllc.taxidi.utils.Extensions.startBaseObservables
import com.lucidsoftworksllc.taxidi.utils.Extensions.toastShort
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import com.lucidsoftworksllc.taxidi.utils.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDriverMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val viewModel : DriverMainActivityViewModel by viewModels {
        ViewModelFactory(
            DriverMainActivityRepository(
                UserPreferences(this@DriverMainActivity),
                RemoteDataSource().buildApi(
                    DriverMainAPI::class.java, authToken = this.getAuthToken
     )))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_main)
        navController = Navigation.findNavController(this, R.id.driver_nav_host_fragment)

        setSupportActionBar(binding.toolbar)
        setupNavigationDrawer()

        val bottomNavigationView = binding.bottomNavigation
        val navView = binding.navView

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            navController.navigate(it.itemId)
            true
        }

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CoroutineScope(Dispatchers.IO).launch {
                    val fbToken = task.result
                    val fcmToken = getFcmToken
                    Log.d("Installations", "Installation auth token: $fbToken, current -> $fcmToken")
                    if (fbToken != fcmToken) {
                        viewModel.sendFCMTokenToServer(fbToken)
                    }
                }
            } else {
                Log.e("Installations", "Unable to get Installation auth token")
            }
        }

        setupViewModelObservables()

    }

    private fun setupViewModelObservables() {
        viewModel.showErrorMessage.observe(this, {
            this.toastShort(it)
        })
        viewModel.showToast.observe(this, {
            this.toastShort(it)
        })
        viewModel.showSnackBar.observe(this, {
            binding.root.snackbar(it, "")
        })
        viewModel.showSnackBarInt.observe(this, {
            binding.root.snackbar(getString(it), "")
        })

        viewModel.navigationCommand.observe(this, { command ->
            when (command) {
                is NavigationCommand.To -> navController.navigate(command.directions)
                is NavigationCommand.Back -> navController.popBackStack()
                is NavigationCommand.BackTo -> navController.popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.driver_nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
                .apply {
                    setStatusBarBackground(R.color.primaryDarkColor)
                }
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}
