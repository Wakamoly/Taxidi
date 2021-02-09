package com.lucidsoftworksllc.taxidi

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.google.maps.GeoApiContext
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.Simulator


class TaxidiApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        Simulator.geoApiContext = GeoApiContext.Builder()
            .apiKey(getString(R.string.directions_google_maps_key))
            .build()
    }

}