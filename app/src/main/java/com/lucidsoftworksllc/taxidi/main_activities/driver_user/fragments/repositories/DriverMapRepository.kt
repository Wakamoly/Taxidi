package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.WebSocketListener
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class DriverMapRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverMapAPI,
    private val networkService: MapNetworkService = MapNetworkService(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher), WebSocketListener {

    override fun onConnect() {
        TODO("Not yet implemented")
    }

    override fun onMessage(data: String) {
        TODO("Not yet implemented")
    }

    override fun onDisconnect() {
        TODO("Not yet implemented")
    }

    override fun onError(error: String) {
        TODO("Not yet implemented")
    }

}
