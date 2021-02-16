package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models

import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
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
        viewState.value = null
        pickUpPath.value = null
        driverLocation.value = null
        directionApiFailedError.value = null
        nearbyCompanies.value = null
        sampleTripPath.value = null
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


}