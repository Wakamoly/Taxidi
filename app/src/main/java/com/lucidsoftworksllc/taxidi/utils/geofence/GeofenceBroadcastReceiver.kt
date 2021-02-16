package com.lucidsoftworksllc.taxidi.utils.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lucidsoftworksllc.taxidi.utils.Extensions

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add start the drive and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Extensions.GeofencingConstants.ACTION_GEOFENCE_EVENT) {
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        }
    }
}