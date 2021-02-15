package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import kotlinx.coroutines.launch

class DriverMapViewModel(
    private val repository: DriverMapRepository
) : BaseViewModel() {

    companion object {
        private const val TAG = "DriverMapViewModel"
    }

    val viewState = repository.viewState
    val pickUpPath = repository.pickUpPath
    val driverLocation = repository.driverLocation
    val directionApiFailedError = repository.directionApiFailedError
    val nearbyCompanies = repository.nearbyCompanies
    val sampleTripPath = repository.sampleTripPath

    override fun onCleared() {
        super.onCleared()
        repository.onDisconnect()
    }

    fun getRoute(origin: LatLng, destination: LatLng) {
        viewModelScope.launch {
            repository.getRoute(origin, destination)
        }
    }

    fun requestNearbyCompanies(latLng: LatLng) {
        viewModelScope.launch {
            repository.requestNearbyCompanies(latLng)
        }
    }

    fun requestCompany(pickUpLatLng: LatLng, dropLatLng: LatLng) {
        viewModelScope.launch {
            repository.requestCompany(pickUpLatLng, dropLatLng)
        }
    }

    fun navigateToShipmentDetails(selectedShipment: CompanyMapMarkerModel?) {
        if (selectedShipment != null) {
            showSnackBar.value = "Selected shipment -> ${selectedShipment.companyName}"
        } else {
            showSnackBar.value = "Selected shipment is null!"
        }
    }


}