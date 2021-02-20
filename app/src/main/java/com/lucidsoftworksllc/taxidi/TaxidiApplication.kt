package com.lucidsoftworksllc.taxidi

import android.app.Application
import android.os.Build
import androidx.work.*
import com.google.android.libraries.places.api.Places
import com.google.maps.GeoApiContext
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.Simulator
import com.lucidsoftworksllc.taxidi.others.RefreshDataWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class TaxidiApplication : Application() {

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        Simulator.geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.directions_google_maps_key))
            .build()
    }

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWork>(
            1,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWork.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

}