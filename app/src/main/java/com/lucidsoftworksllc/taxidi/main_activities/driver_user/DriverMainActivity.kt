package com.lucidsoftworksllc.taxidi.main_activities.driver_user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.databinding.ActivityDriverMainBinding
import com.lucidsoftworksllc.taxidi.db.TaxidiDatabase

class DriverMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDriverMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

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
        //navController.navigate(item.itemId)
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
