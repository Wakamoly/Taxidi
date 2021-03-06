package com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import com.lucidsoftworksllc.taxidi.utils.Constants.AT_PICKUP
import com.lucidsoftworksllc.taxidi.utils.Constants.COMPANY_ID
import com.lucidsoftworksllc.taxidi.utils.Constants.COMPANY_IMAGE
import com.lucidsoftworksllc.taxidi.utils.Constants.COMPANY_NAME
import com.lucidsoftworksllc.taxidi.utils.Constants.DIRECTION_API_FAILED
import com.lucidsoftworksllc.taxidi.utils.Constants.DISTANCE
import com.lucidsoftworksllc.taxidi.utils.Constants.DRIVER_ARRIVED
import com.lucidsoftworksllc.taxidi.utils.Constants.DRIVER_IS_ARRIVING
import com.lucidsoftworksllc.taxidi.utils.Constants.ERROR
import com.lucidsoftworksllc.taxidi.utils.Constants.LAT
import com.lucidsoftworksllc.taxidi.utils.Constants.LNG
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_BOOKED
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_ID
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_IMAGE
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_PAY
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_TYPE
import com.lucidsoftworksllc.taxidi.utils.Constants.LOAD_WEIGHT
import com.lucidsoftworksllc.taxidi.utils.Constants.LOCATION
import com.lucidsoftworksllc.taxidi.utils.Constants.LOCATIONS
import com.lucidsoftworksllc.taxidi.utils.Constants.NEARBY_COMPANIES
import com.lucidsoftworksllc.taxidi.utils.Constants.PATH
import com.lucidsoftworksllc.taxidi.utils.Constants.PICKUP_PATH
import com.lucidsoftworksllc.taxidi.utils.Constants.ROUTES_NOT_AVAILABLE
import com.lucidsoftworksllc.taxidi.utils.Constants.SAMPLE_TRIP_PATH
import com.lucidsoftworksllc.taxidi.utils.Constants.TO_LAT
import com.lucidsoftworksllc.taxidi.utils.Constants.TO_LNG
import com.lucidsoftworksllc.taxidi.utils.Constants.TRAILER_TYPE
import com.lucidsoftworksllc.taxidi.utils.Constants.TRIP_END
import com.lucidsoftworksllc.taxidi.utils.Constants.TRIP_PATH
import com.lucidsoftworksllc.taxidi.utils.Constants.TRIP_START
import com.lucidsoftworksllc.taxidi.utils.Constants.TYPE
import com.lucidsoftworksllc.taxidi.utils.Extensions.latLngToLatLngForSomeReason
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getDistanceFromBothLatLngs
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomCompanyID
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomCompanyImage
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomCompanyName
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomLoadImage
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomLoadPay
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomLoadType
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomLoadWeight
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomToLatLng
import com.lucidsoftworksllc.taxidi.utils.MapUtils.getRandomTrailerTypeNeeded
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

object Simulator {

    private const val TAG = "Simulator"
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    lateinit var geoApiContext: GeoApiContext
    private lateinit var currentLocation: LatLng
    private lateinit var pickUpLocation: LatLng
    private lateinit var dropLocation: LatLng
    private var nearbyCompanies = arrayListOf<CompanyMapMarkerModel>()
    private var pickUpPath = arrayListOf<com.google.maps.model.LatLng>()
    private var tripPath = arrayListOf<com.google.maps.model.LatLng>()
    private val mainThread = Handler(Looper.getMainLooper())

    fun getFakeNearbyCompanies(
        latitude: Double,
        longitude: Double,
        webSocketListener: WebSocketListener
    ) {
        nearbyCompanies.clear()
        currentLocation = LatLng(latitude, longitude)
        val size = (10..25).random()

        for (i in 1..size) {
            /**
             * Random LatLng
             */
            val randomOperatorForLat = (0..1).random()
            val randomOperatorForLng = (0..1).random()
            var randomDeltaForLat = (1..50).random() / 1000.00
            var randomDeltaForLng = (1..50).random() / 1000.00
            if (randomOperatorForLat == 1) {
                randomDeltaForLat *= -1
            }
            if (randomOperatorForLng == 1) {
                randomDeltaForLng *= -1
            }
            val randomLatitude = (latitude + randomDeltaForLat).coerceAtMost(360.00)
            val randomLongitude = (longitude + randomDeltaForLng).coerceAtMost(360.00)
            val latLng = LatLng(randomLatitude, randomLongitude)

            /**
             * Random company name
             */
            val companyName = getRandomCompanyName()

            /**
             * Random company ID
             */
            val companyID = getRandomCompanyID()

            /**
             * Random company photo
             */
            val companyImage = getRandomCompanyImage()

            /**
             * Random load image
             */
            val loadImage = getRandomLoadImage()

            /**
             * Load type
             */
            val loadType = getRandomLoadType()

            /**
             * Random load weight in lbs
             */
            val loadWeight = getRandomLoadWeight()

            /**
             * Random load pay
             */
            val loadPay = getRandomLoadPay()

            /**
             * Random trailer type need
             */
            val trailerType = getRandomTrailerTypeNeeded()

            /**
             * Random toLatLng
             */
            val toLatLng = getRandomToLatLng(currentLocation)

            /**
             * Distance between both LatLngs
             */
            val distance = getDistanceFromBothLatLngs(latLng, toLatLng)

            /**
             * Random load ID
             */
            val loadID = getRandomCompanyID()

            val newCompany = CompanyMapMarkerModel(
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
            nearbyCompanies.add(newCompany)
        }

        val jsonObjectToPush = JSONObject()
        jsonObjectToPush.put(TYPE, NEARBY_COMPANIES)
        val jsonArray = JSONArray()
        for (company in nearbyCompanies) {
            val jsonObjectLatLng = JSONObject()
            jsonObjectLatLng.put(LAT, company.latLng.latitude)
            jsonObjectLatLng.put(LNG, company.latLng.longitude)
            jsonObjectLatLng.put(COMPANY_NAME, company.companyName)
            jsonObjectLatLng.put(COMPANY_ID, company.companyId)
            jsonObjectLatLng.put(COMPANY_IMAGE, company.companyImage)
            jsonObjectLatLng.put(LOAD_IMAGE, company.loadImage)
            jsonObjectLatLng.put(LOAD_TYPE, company.loadType)
            jsonObjectLatLng.put(LOAD_WEIGHT, company.loadWeight)
            jsonObjectLatLng.put(LOAD_PAY, company.loadPay)
            jsonObjectLatLng.put(TRAILER_TYPE, company.trailerType)
            jsonObjectLatLng.put(TO_LAT, company.toLatLng.latitude)
            jsonObjectLatLng.put(TO_LNG, company.toLatLng.longitude)
            jsonObjectLatLng.put(DISTANCE, company.distance)
            jsonObjectLatLng.put(LOAD_ID, company.loadId)
            jsonArray.put(jsonObjectLatLng)
        }
        jsonObjectToPush.put(LOCATIONS, jsonArray)
        mainThread.post {
            webSocketListener.onMessage(jsonObjectToPush.toString())
        }
    }

    fun requestCompany(
            pickUpLocation: LatLng,
            dropLocation: LatLng,
            webSocketListener: WebSocketListener
    ) {
        Simulator.pickUpLocation = pickUpLocation
        Simulator.dropLocation = dropLocation

        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(latLngToLatLngForSomeReason(currentLocation))
        directionsApiRequest.destination(latLngToLatLngForSomeReason(Simulator.pickUpLocation))
        directionsApiRequest.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d(TAG, "onResult : $result")
                val jsonObjectCompanyBooked = JSONObject()
                jsonObjectCompanyBooked.put(TYPE, LOAD_BOOKED)
                mainThread.post {
                    webSocketListener.onMessage(jsonObjectCompanyBooked.toString())
                }
                pickUpPath.clear()
                val routeList = result.routes
                // will have zero or 1 route as we haven't asked Google API for multiple paths

                if (routeList.isEmpty()) {
                    val jsonObjectFailure = JSONObject()
                    jsonObjectFailure.put(TYPE, ROUTES_NOT_AVAILABLE)
                    mainThread.post {
                        webSocketListener.onError(jsonObjectFailure.toString())
                    }
                } else {
                    for (route in routeList) {
                        val path = route.overviewPolyline.decodePath()
                        pickUpPath.addAll(path)
                    }

                    val jsonObject = JSONObject()
                    jsonObject.put(TYPE, PICKUP_PATH)
                    val jsonArray = JSONArray()
                    for (pickUp in pickUpPath) {
                        val jsonObjectLatLng = JSONObject()
                        jsonObjectLatLng.put(LAT, pickUp.lat)
                        jsonObjectLatLng.put(LNG, pickUp.lng)
                        jsonArray.put(jsonObjectLatLng)
                    }
                    jsonObject.put(PATH, jsonArray)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObject.toString())
                    }

                    startTimerForPickUp(webSocketListener)
                }

            }

            override fun onFailure(e: Throwable) {
                Log.d(TAG, "onFailure : ${e.message}")
                val jsonObjectFailure = JSONObject()
                jsonObjectFailure.put(TYPE, DIRECTION_API_FAILED)
                jsonObjectFailure.put(ERROR, e.message)
                mainThread.post {
                    webSocketListener.onError(jsonObjectFailure.toString())
                }
            }
        })
    }

    fun startTimerForPickUp(webSocketListener: WebSocketListener) {
        val delay = 2000L
        val period = 3000L
        val size = pickUpPath.size
        var index = 0
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                val jsonObject = JSONObject()
                jsonObject.put(TYPE, LOCATION)
                jsonObject.put(LAT, pickUpPath[index].lat)
                jsonObject.put(LNG, pickUpPath[index].lng)
                mainThread.post {
                    webSocketListener.onMessage(jsonObject.toString())
                }

                if (index == size - 1) {
                    stopTimer()
                    val jsonObjectDriverIsArriving = JSONObject()
                    jsonObjectDriverIsArriving.put(TYPE, DRIVER_IS_ARRIVING)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObjectDriverIsArriving.toString())
                    }
                    startTimerForWaitDuringPickUp(webSocketListener)
                }

                index++
            }
        }

        timer?.schedule(timerTask, delay, period)
    }

    fun startTimerForWaitDuringPickUp(webSocketListener: WebSocketListener) {
        val delay = 3000L
        val period = 3000L
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                stopTimer()
                val jsonObjectCabArrived = JSONObject()
                jsonObjectCabArrived.put(TYPE, DRIVER_ARRIVED)
                mainThread.post {
                    webSocketListener.onMessage(jsonObjectCabArrived.toString())
                }
                val directionsApiRequest = DirectionsApiRequest(geoApiContext)
                directionsApiRequest.mode(TravelMode.DRIVING)
                directionsApiRequest.origin(latLngToLatLngForSomeReason(pickUpLocation))
                directionsApiRequest.destination(latLngToLatLngForSomeReason(dropLocation))
                directionsApiRequest.setCallback(object :
                    PendingResult.Callback<DirectionsResult> {
                    override fun onResult(result: DirectionsResult) {
                        Log.d(TAG, "onResult : $result")
                        tripPath.clear()
                        val routeList = result.routes
                        // Actually it will have zero or 1 route as we haven't asked Google API for multiple paths

                        if (routeList.isEmpty()) {
                            val jsonObjectFailure = JSONObject()
                            jsonObjectFailure.put(TYPE, ROUTES_NOT_AVAILABLE)
                            mainThread.post {
                                webSocketListener.onError(jsonObjectFailure.toString())
                            }
                        } else {
                            for (route in routeList) {
                                val path = route.overviewPolyline.decodePath()
                                tripPath.addAll(path)
                            }
                            val jsonObject2 = JSONObject()
                            jsonObject2.put(TYPE, AT_PICKUP)
                            mainThread.post {
                                webSocketListener.onMessage(jsonObject2.toString())
                            }
                        }

                    }

                    override fun onFailure(e: Throwable) {
                        Log.d(TAG, "onFailure : ${e.message}")
                        val jsonObjectFailure = JSONObject()
                        jsonObjectFailure.put(TYPE, DIRECTION_API_FAILED)
                        jsonObjectFailure.put(ERROR, e.message)
                        mainThread.post {
                            webSocketListener.onError(jsonObjectFailure.toString())
                        }
                    }
                })

            }
        }
        timer?.schedule(timerTask, delay, period)
    }

    fun startTimerForTrip(webSocketListener: WebSocketListener) {
        val delay = 5000L
        val period = 3000L
        val size = tripPath.size
        var index = 0
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {

                if (index == 0) {
                    val jsonObjectTripStart = JSONObject()
                    jsonObjectTripStart.put(TYPE, TRIP_START)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObjectTripStart.toString())
                    }

                    val jsonObject = JSONObject()
                    jsonObject.put(TYPE, TRIP_PATH)
                    val jsonArray = JSONArray()
                    for (trip in tripPath) {
                        val jsonObjectLatLng = JSONObject()
                        jsonObjectLatLng.put(LAT, trip.lat)
                        jsonObjectLatLng.put(LNG, trip.lng)
                        jsonArray.put(jsonObjectLatLng)
                    }
                    jsonObject.put(PATH, jsonArray)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObject.toString())
                    }
                }

                val jsonObject = JSONObject()
                jsonObject.put(TYPE, LOCATION)
                jsonObject.put(LAT, tripPath[index].lat)
                jsonObject.put(LNG, tripPath[index].lng)
                mainThread.post {
                    webSocketListener.onMessage(jsonObject.toString())
                }

                if (index == size - 1) {
                    stopTimer()
                    startTimerForTripEndEvent(webSocketListener)
                }

                index++
            }
        }
        timer?.schedule(timerTask, delay, period)
    }

    fun startTimerForTripEndEvent(webSocketListener: WebSocketListener) {
        val delay = 3000L
        val period = 3000L
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                stopTimer()
                val jsonObjectTripEnd = JSONObject()
                jsonObjectTripEnd.put(TYPE, TRIP_END)
                val jsonObject2 = JSONObject()
                jsonObject2.put(TYPE, AT_PICKUP)
                mainThread.post {
                    webSocketListener.onMessage(jsonObjectTripEnd.toString())
                    webSocketListener.onMessage(jsonObject2.toString())
                    // TODO: 2/16/2021 Use repository to tell the server the load has been finished
                }
            }
        }
        timer?.schedule(timerTask, delay, period)
    }

    fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    fun getRoute(origin: LatLng, destination: LatLng, webSocketListener: WebSocketListener) {
        val directionsApiRequest = DirectionsApiRequest(geoApiContext)
        directionsApiRequest.mode(TravelMode.DRIVING)
        directionsApiRequest.origin(latLngToLatLngForSomeReason(origin))
        directionsApiRequest.destination(latLngToLatLngForSomeReason(destination))
        directionsApiRequest.setCallback(object :
                PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                Log.d(TAG, "DirectionsResult : ${result.routes.size}")
                tripPath.clear()
                val routeList = result.routes
                // Actually it will have zero or 1 route as we haven't asked Google API for multiple paths

                if (routeList.isEmpty()) {
                    Log.d(TAG, "Routelist empty! OriginLat : ${origin.latitude}")
                    Log.d(TAG, "OriginLng : ${origin.longitude}")
                    Log.d(TAG, "DestLat : ${destination.latitude}")
                    Log.d(TAG, "DestLng : ${destination.longitude}")
                    val jsonObjectFailure = JSONObject()
                    jsonObjectFailure.put(TYPE, ROUTES_NOT_AVAILABLE)
                    mainThread.post {
                        webSocketListener.onError(jsonObjectFailure.toString())
                    }
                } else {
                    for (route in routeList) {
                        val path = route.overviewPolyline.decodePath()
                        tripPath.addAll(path)
                    }

                    val jsonObject = JSONObject()
                    jsonObject.put(TYPE, SAMPLE_TRIP_PATH)
                    val jsonArray = JSONArray()
                    for (trip in tripPath) {
                        val jsonObjectLatLng = JSONObject()
                        jsonObjectLatLng.put(LAT, trip.lat)
                        jsonObjectLatLng.put(LNG, trip.lng)
                        jsonArray.put(jsonObjectLatLng)
                    }
                    jsonObject.put(PATH, jsonArray)
                    mainThread.post {
                        webSocketListener.onMessage(jsonObject.toString())
                    }

                }

            }

            override fun onFailure(e: Throwable?) {
                Log.d(TAG, "onFailure : ${e?.message}")
                val jsonObjectFailure = JSONObject()
                jsonObjectFailure.put(TYPE, DIRECTION_API_FAILED)
                jsonObjectFailure.put(ERROR, e?.message)
                mainThread.post {
                    webSocketListener.onError(jsonObjectFailure.toString())
                }
            }
        })
    }

}