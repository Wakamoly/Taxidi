package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.base.BaseRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.WebSocket
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.WebSocketListener
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import com.lucidsoftworksllc.taxidi.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject

class DriverMapRepository (
    private val userPreferences: UserPreferences,
    private val api: DriverMapAPI,
    networkService: MapNetworkService = MapNetworkService(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseRepository(dispatcher), WebSocketListener {

    companion object {
        private const val TAG = "DriverMapRepo"
    }

    val viewState = MutableLiveData<Int>()
    val pickUpPath = MutableLiveData<ArrayList<LatLng>>()
    val driverLocation = MutableLiveData<LatLng>()
    val directionApiFailedError = MutableLiveData<String>()
    val nearbyCompanies = MutableLiveData<ArrayList<CompanyMapMarkerModel>>()
    val sampleTripPath = MutableLiveData<ArrayList<LatLng>>()

    private var webSocket: WebSocket = networkService.createWebSocket(this)

    init {
        webSocket.connect()
    }

    fun getRoute(origin: LatLng, destination: LatLng) {
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.REQUEST_ROUTE)
        jsonObject.put(Constants.ORIGIN_LAT, origin.latitude)
        jsonObject.put(Constants.ORIGIN_LNG, origin.longitude)
        jsonObject.put(Constants.DEST_LAT, destination.latitude)
        jsonObject.put(Constants.DEST_LNG, destination.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    fun requestNearbyCompanies(latLng: LatLng) {
        Log.d(TAG, "requestNearbyCompanies")
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.NEARBY_COMPANIES)
        jsonObject.put(Constants.LAT, latLng.latitude)
        jsonObject.put(Constants.LNG, latLng.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    fun requestCompany(pickUpLatLng: LatLng, dropLatLng: LatLng) {
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.REQUEST_COMPANY)
        jsonObject.put(Constants.PICKUP_LAT, pickUpLatLng.latitude)
        jsonObject.put(Constants.PICKUP_LNG, pickUpLatLng.longitude)
        jsonObject.put(Constants.DROP_LAT, dropLatLng.latitude)
        jsonObject.put(Constants.DROP_LNG, dropLatLng.longitude)
        webSocket.sendMessage(jsonObject.toString())
    }

    fun finishedPickup() {
        val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.FINISHED_PICKUP)
        webSocket.sendMessage(jsonObject.toString())
    }

    fun finishedDropOff() {
        // TODO: 2/16/2021 Send stuff to server
        /*val jsonObject = JSONObject()
        jsonObject.put(Constants.TYPE, Constants.FINISHED_DROPOFF)
        webSocket.sendMessage(jsonObject.toString())*/
    }

    override fun onConnect() {
        Log.d(TAG, "onConnect")
    }

    private fun handleOnMessageNearbyCompanies(jsonObject: JSONObject) {
        val nearbyCompanies = arrayListOf<CompanyMapMarkerModel>()
        val jsonArray = jsonObject.getJSONArray(Constants.LOCATIONS)
        for (i in 0 until jsonArray.length()) {

            val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
            val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
            val companyName = (jsonArray.get(i) as JSONObject).getString(Constants.COMPANY_NAME)
            val companyID = (jsonArray.get(i) as JSONObject).getLong(Constants.COMPANY_ID)
            val companyImage = (jsonArray.get(i) as JSONObject).getString(Constants.COMPANY_IMAGE)
            val loadImage = (jsonArray.get(i) as JSONObject).getString(Constants.LOAD_IMAGE)
            val loadType = (jsonArray.get(i) as JSONObject).getInt(Constants.LOAD_TYPE)
            val loadWeight = (jsonArray.get(i) as JSONObject).getDouble(Constants.LOAD_WEIGHT)
            val loadPay = (jsonArray.get(i) as JSONObject).getDouble(Constants.LOAD_PAY)
            val trailerType = (jsonArray.get(i) as JSONObject).getInt(Constants.TRAILER_TYPE)
            val toLat = (jsonArray.get(i) as JSONObject).getDouble(Constants.TO_LAT)
            val toLng = (jsonArray.get(i) as JSONObject).getDouble(Constants.TO_LNG)
            val distance = (jsonArray.get(i) as JSONObject).getString(Constants.DISTANCE)
            val loadID = (jsonArray.get(i) as JSONObject).getLong(Constants.LOAD_ID)

            val latLng = LatLng(lat, lng)
            val toLatLng = LatLng(toLat, toLng)

            val company = CompanyMapMarkerModel(
                latLng,
                companyName,
                companyID,
                companyImage,
                loadImage,
                loadType,
                loadWeight,
                loadPay,
                trailerType,
                toLatLng,
                distance,
                loadID
            )
            nearbyCompanies.add(company)
        }
        this.nearbyCompanies.value = nearbyCompanies
        viewState.value = 10
    }

    override fun onMessage(data: String) {
        Log.d(TAG, "onMessage data : $data")
        val jsonObject = JSONObject(data)
        when (jsonObject.getString(Constants.TYPE)) {
            Constants.NEARBY_COMPANIES -> {
                handleOnMessageNearbyCompanies(jsonObject)
            }
            Constants.LOAD_BOOKED -> {
                viewState.value = 1
            }
            Constants.PICKUP_PATH, Constants.TRIP_PATH -> {
                val jsonArray = jsonObject.getJSONArray(Constants.PATH)
                val pickUpPath = arrayListOf<LatLng>()
                for (i in 0 until jsonArray.length()) {
                    val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
                    val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
                    val latLng = LatLng(lat, lng)
                    pickUpPath.add(latLng)
                }
                this.pickUpPath.value = pickUpPath
                viewState.value = 2
            }
            Constants.LOCATION -> {
                val latCurrent = jsonObject.getDouble(Constants.LAT)
                val lngCurrent = jsonObject.getDouble(Constants.LNG)
                driverLocation.value = LatLng(latCurrent, lngCurrent)
                viewState.value = 3
            }
            Constants.DRIVER_IS_ARRIVING -> {
                viewState.value = 4
            }
            Constants.DRIVER_ARRIVED -> {
                viewState.value = 5
            }
            Constants.TRIP_START -> {
                viewState.value = 6
            }
            Constants.TRIP_END -> {
                viewState.value = 7
            }
            Constants.SAMPLE_TRIP_PATH -> {
                val jsonArray = jsonObject.getJSONArray(Constants.PATH)
                val sampleTripPath = arrayListOf<LatLng>()
                for (i in 0 until jsonArray.length()) {
                    val lat = (jsonArray.get(i) as JSONObject).getDouble(Constants.LAT)
                    val lng = (jsonArray.get(i) as JSONObject).getDouble(Constants.LNG)
                    val latLng = LatLng(lat, lng)
                    sampleTripPath.add(latLng)
                }
                this.sampleTripPath.value = sampleTripPath
                viewState.value = 11
            }
            Constants.AT_PICKUP -> {
                viewState.value = 12
            }
            Constants.AT_DROPOFF -> {
                viewState.value = 13
            }
        }
    }

    override fun onDisconnect() {
        Log.d(TAG, "onDisconnect")
        webSocket.disconnect()
    }

    override fun onError(error: String) {
        Log.d(TAG, "onError : $error")
        val jsonObject = JSONObject(error)
        when (jsonObject.getString(Constants.TYPE)) {
            Constants.ROUTES_NOT_AVAILABLE -> {
                viewState.value = 8
            }
            Constants.DIRECTION_API_FAILED -> {
                directionApiFailedError.value = "Direction API Failed : " + jsonObject.getString(Constants.ERROR)
                viewState.value = 9
            }
        }
    }

}
