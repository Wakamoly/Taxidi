package com.lucidsoftworksllc.taxidi.utils

object Constants {
    // URL constants, Sabot is a pet-project of mine, I'll be using the existing domain for this project.
    const val BASE_URL = "https://www.sabotcommunity.com/"
    private const val APP_VERSION_FINAL = "v0.0.1a"
    private const val APP_VERSION_URL = "$APP_VERSION_FINAL/"
    const val ROOT_URL = BASE_URL + "taxidi/" + APP_VERSION_URL

    // TODO Use this installed version to cross-reference with newest version uploaded to notify user a newer version is available.
    const val APP_GRADLE_VERSION = 1

    const val TYPE = "type"
    const val PATH = "path"
    const val NEARBY_COMPANIES = "nearByCompanies"
    const val LOAD_BOOKED = "loadBooked"
    const val PICKUP_PATH = "pickUpPath"
    const val TRIP_PATH = "tripPath"
    const val LOCATIONS = "locations"
    const val LOCATION = "location"
    const val DRIVER_IS_ARRIVING = "driverIsArriving"
    const val DRIVER_ARRIVED = "cabArrived"
    const val TRIP_START = "tripStart"
    const val TRIP_END = "tripEnd"
    const val LAT = "lat"
    const val LNG = "lng"
    const val ROUTES_NOT_AVAILABLE = "routesNotAvailable"
    const val DIRECTION_API_FAILED = "directionApiFailed"
    const val ERROR = "error"
    const val PICKUP_LAT = "pickUpLat"
    const val PICKUP_LNG = "pickUpLng"
    const val DROP_LAT = "dropLat"
    const val DROP_LNG = "dropLng"
    const val ORIGIN_LAT = "originLat"
    const val ORIGIN_LNG = "origiLng"
    const val DEST_LAT = "destLat"
    const val DEST_LNG = "destLng"
    const val REQUEST_COMPANY = "requestCompany"
    const val REQUEST_ROUTE = "requestRoute"
    const val SAMPLE_TRIP_PATH = "sampleTripPath"
    const val AT_PICKUP = "driverAtPickup"
    const val AT_DROPOFF = "driverAtDropOff"
    const val FINISHED_PICKUP = "finishedPickup"
    const val FINISHED_DROPOFF = "finishedDropOff"

    const val COMPANY_NAME = "companyName"
    const val COMPANY_ID = "companyID"
    const val COMPANY_IMAGE = "companyImage"
    const val LOAD_IMAGE = "loadImage"
    const val LOAD_TYPE = "loadType"
    const val LOAD_WEIGHT = "loadWeight"
    const val LOAD_PAY = "loadPay"
    const val TRAILER_TYPE = "trailerType"
    const val TO_LAT = "toLat"
    const val TO_LNG = "toLng"
    const val DISTANCE = "distance"
    const val LOAD_ID = "loadID"

}