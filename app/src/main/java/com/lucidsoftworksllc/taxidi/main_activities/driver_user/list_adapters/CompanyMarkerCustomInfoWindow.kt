package com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.databinding.ItemCompanyMapMarkerBinding
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import com.lucidsoftworksllc.taxidi.utils.Constants


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

        /**
         * View binding with Glide on image views won't work on info windows, using a listener to reload the window seamlessly on success
         */
        if (infoWindowData?.loadImage != null) {
            val finalImageUrl = Constants.BASE_URL + infoWindowData.loadImage
            Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .listener(MarkerCallback(marker))
                .into(binding.loadImage)
        }

        if (infoWindowData?.companyImage != null) {
            val finalImageUrl = Constants.BASE_URL + infoWindowData.companyImage
            Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.ic_baseline_broken_image_24)
                .listener(MarkerCallback(marker))
                .into(binding.companyProfileImage)
        }

        return binding.root
    }

    class MarkerCallback internal constructor(marker: Marker?) :
        RequestListener<Drawable> {

        var marker: Marker? = null

        private fun onSuccess() {
            if (marker != null && marker!!.isInfoWindowShown) {
                marker!!.hideInfoWindow()
                marker!!.showInfoWindow()
            }
        }

        init {
            this.marker = marker
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            Log.e(javaClass.simpleName, "Error loading thumbnail! -> $e")
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            onSuccess()
            return false
        }
    }

}