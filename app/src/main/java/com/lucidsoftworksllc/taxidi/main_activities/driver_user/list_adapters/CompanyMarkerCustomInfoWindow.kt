package com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.text.method.TextKeyListener.clear
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lucidsoftworksllc.taxidi.databinding.ItemCompanyMapMarkerBinding
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel


class CompanyMarkerCustomInfoWindow(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val layoutInflater = (context as Activity).layoutInflater
        val binding = ItemCompanyMapMarkerBinding.inflate(layoutInflater)
        val infoWindowData: CompanyMapMarkerModel? = marker.tag as CompanyMapMarkerModel?
        binding.model = infoWindowData
        binding.executePendingBindings()
        //Glide.with(context).load(infoWindowData?.loadImage).into(binding.loadImage)
        //Glide.with(context).load(infoWindowData?.companyImage).into(binding.companyProfileImage)

        return binding.root
    }

}