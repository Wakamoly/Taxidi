package com.lucidsoftworksllc.taxidi.others.models.server_responses

import com.google.android.gms.maps.model.LatLng

data class CompanyMapMarkerModel(
    val latLng: LatLng,
    val companyName: String,
    val companyId: Long,
    val companyImage: String,
    val loadImage: String,
    val loadType: Int,
    val loadWeight: Double,
    val loadPay: Double,
    val trailerType: Int,
    val toLatLng: LatLng,
    val distance: String,
    val loadId: Long
)
