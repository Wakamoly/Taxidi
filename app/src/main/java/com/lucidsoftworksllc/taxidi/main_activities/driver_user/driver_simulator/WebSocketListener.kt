package com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator

interface WebSocketListener {

    fun onConnect()

    fun onMessage(data: String)

    fun onDisconnect()

    fun onError(error: String)

}