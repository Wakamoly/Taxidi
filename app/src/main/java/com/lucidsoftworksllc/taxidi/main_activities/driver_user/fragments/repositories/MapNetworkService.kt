package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.WebSocket
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.WebSocketListener

class MapNetworkService {

    fun createWebSocket(webSocketListener: WebSocketListener): WebSocket {
        return WebSocket(webSocketListener)
    }

}