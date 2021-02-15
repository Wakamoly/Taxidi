package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api

import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel

interface DriverMapsView {

    fun showNearbyCompanies(companies: List<CompanyMapMarkerModel>?)

    fun informCompanyBooked()

    fun showPath(latLngList: List<LatLng>?)

    fun updateSimulatedDriverLocation(latLng: LatLng?)

    fun informDriverIsArriving()

    fun informDriverArrived()

    fun informTripStart()

    fun informTripEnd()

    fun showRoutesNotAvailableError()

    fun showDirectionApiFailedError(error: String?)


}