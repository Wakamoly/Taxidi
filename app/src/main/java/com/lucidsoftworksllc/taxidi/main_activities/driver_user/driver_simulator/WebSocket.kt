package com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator

import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.utils.Constants.DROP_LAT
import com.lucidsoftworksllc.taxidi.utils.Constants.DROP_LNG
import com.lucidsoftworksllc.taxidi.utils.Constants.LAT
import com.lucidsoftworksllc.taxidi.utils.Constants.LNG
import com.lucidsoftworksllc.taxidi.utils.Constants.NEARBY_COMPANIES
import com.lucidsoftworksllc.taxidi.utils.Constants.PICKUP_LAT
import com.lucidsoftworksllc.taxidi.utils.Constants.PICKUP_LNG
import com.lucidsoftworksllc.taxidi.utils.Constants.REQUEST_COMPANY
import com.lucidsoftworksllc.taxidi.utils.Constants.TYPE
import org.json.JSONObject

class WebSocket(private var webSocketListener: WebSocketListener) {

    fun connect() {
        webSocketListener.onConnect()
    }

    fun sendMessage(data: String) {
        val jsonObject = JSONObject(data)
        when (jsonObject.getString(TYPE)) {
            NEARBY_COMPANIES -> {
                Simulator.getFakeNearbyCompanies(
                    jsonObject.getDouble(LAT),
                    jsonObject.getDouble(LNG),
                    webSocketListener
                )
            }
            REQUEST_COMPANY -> {
                val pickUpLatLng =
                    LatLng(jsonObject.getDouble(PICKUP_LAT), jsonObject.getDouble(PICKUP_LNG))
                val dropLatLng =
                    LatLng(jsonObject.getDouble(DROP_LAT), jsonObject.getDouble(DROP_LNG))
                Simulator.requestCompany(
                    pickUpLatLng,
                    dropLatLng,
                    webSocketListener
                )
            }
        }
    }

    fun disconnect() {
        Simulator.stopTimer()
        this.webSocketListener.onDisconnect()
    }

}