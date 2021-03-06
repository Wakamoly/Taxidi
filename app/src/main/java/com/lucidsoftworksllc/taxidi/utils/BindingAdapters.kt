package com.lucidsoftworksllc.taxidi.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter
import com.lucidsoftworksllc.taxidi.utils.Extensions.fadeIn
import com.lucidsoftworksllc.taxidi.utils.Extensions.fadeOut
import com.lucidsoftworksllc.taxidi.utils.Extensions.getTimeAgo
import de.hdodenhof.circleimageview.CircleImageView


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
    @Suppress("UNCHECKED_CAST")
    @BindingAdapter("android:liveData")
    @JvmStatic
    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
        items?.value?.let { itemList ->
            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
                clear()
                addData(itemList)
            }
        }
    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("app:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.fadeIn()
            } else {
                if (view.visibility == View.VISIBLE)
                    view.fadeOut()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:loadImageNoAnim")
    fun setImageResourceNoAnim(view: ImageView, imageUrl: String?) {
        val context = view.context
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        imageUrl?.let {
            val finalImageUrl = Constants.BASE_URL + imageUrl
            Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(finalImageUrl)
                .error(R.drawable.ic_baseline_broken_image_24)
                .fallback(R.drawable.ic_baseline_broken_image_24)
                // transition crossfade has some strange bugs with other animations
                //.transition(DrawableTransitionOptions.withCrossFade(500))
                .into(view)
            view.requestLayout()
        }
    }

    @JvmStatic
    @BindingAdapter("app:loadImage")
    fun setImageResource(view: ImageView, imageUrl: String?) {
        val context = view.context
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        imageUrl?.let {
            val finalImageUrl = Constants.BASE_URL + imageUrl
            Glide.with(context)
                .setDefaultRequestOptions(options)
                .load(finalImageUrl)
                // transition crossfade has some strange bugs with other animations
                .transition(DrawableTransitionOptions.withCrossFade(1000))
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("app:userStatusInt")
    fun setUserStatusDrawable(view: ImageView, statusInt: Int?) {
        statusInt?.let {
            val context = view.context
            var drawable: Drawable? = null
            when (it) {
                // 0 is inactive
                // 1 is idle
                // 2 is active
                0 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_offline_24) }
                1 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_online_idle_24) }
                2 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_online_active_24) }
            }
            view.setImageDrawable(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("app:userStatusIntToText")
    fun setUserStatusText(view: TextView, statusInt: Int?) {
        statusInt?.let {
            val context = view.context
            val text: String = when (it) {
                // 0 is inactive
                // 1 is idle
                // 2 is active
                0 -> { context.getString(R.string.user_inactive) }
                1 -> { context.getString(R.string.user_idle) }
                2 -> { context.getString(R.string.user_active) }
                else -> { context.getString(R.string.srverr_unknown) }
            }
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:srb_vmSetRating")
    fun setRatingFromXML(view: SimpleRatingBar, value: Float?) {
        value?.let {
            view.rating = value
        }
    }

    @JvmStatic
    @BindingAdapter("app:tvVisibility")
    fun setTextViewVisibleIfNotEmpty(view: TextView, value: String?) {
        value?.let {
            if (!value.isNullOrEmpty()){
                view.visibility = View.VISIBLE
                view.text = value
            } else {
                view.visibility = View.GONE
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:tvTimeSince")
    fun setTimeSinceText(view: TextView, value: String?) {
        value?.let {
            if (!value.isNullOrEmpty()){
                view.text = getTimeAgo(value, view.context)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:tvTotalRatings")
    fun setTotalNumReviews(view: TextView, value: Int?) {
        value?.let {
            val text = "$value ${view.context.getString(R.string.reviews_total_num_concat_text)}"
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:tvTotalShipments")
    fun setTotalNumShipments(view: TextView, value: Int?) {
        value?.let {
            val text = "$value ${view.context.getString(R.string.shipments_total_num_concat_text)}"
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:verifiedImage")
    fun setVerifiedDrawable(view: ImageView, value: Int?) {
        value?.let {
            val context = view.context
            val drawable = when (it) {
                // 0 is not verified
                // 1 is verified
                // plans for more values
                0 -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_nope_red_24)
                }
                1 -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_check_circle_green_24)
                }
                else -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_check_circle_green_24)
                }
            }
            view.setImageDrawable(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("app:verifiedText")
    fun setVerifiedText(view: TextView, value: Int?) {
        value?.let {
            val context = view.context
            val text = when (it) {
                // 0 is not verified
                // 1 is verified
                // plans for more values
                0 -> {
                    context.getString(R.string.not_verified_text)
                }
                1 -> {
                    context.getString(R.string.is_verified_text)
                }
                else -> {
                    context.getString(R.string.is_verified_text)
                }
            }
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:notificationType")
    fun setNotificationTypeDrawable(view: ImageView, value: String?) {
        value?.let {
            val context = view.context
            val drawable = when (it) {
                "reviewed" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_star_rate_24)
                }
                "leave_a_review" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_stars_24)
                }
                "load_proposition" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_store_24)
                }
                "load_in_progress" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_add_location_24_green)
                }
                "load_picked_up" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_forward_to_inbox_24_blue)
                }
                "load_completed" -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_check_circle_green_24)
                }
                else -> {
                    ContextCompat.getDrawable(context, R.drawable.ic_baseline_contact_support_24)
                }
            }
            view.setImageDrawable(drawable)
        }
    }

    @JvmStatic
    @BindingAdapter("app:notificationSeen")
    fun setNotiViewBackground(view: ViewGroup, value: Int?) {
        value?.let {
            val context = view.context
            var color = 0
            when (it) {
                // 0 is unopened
                // 1 is opened
                0 -> { color = ContextCompat.getColor(context, R.color.primaryDarkColor) }
                1 -> { color = ContextCompat.getColor(context, R.color.primaryColor) }
            }
            view.setBackgroundColor(color)
        }
    }

    @JvmStatic
    @BindingAdapter("app:notificationSeenCivBG")
    fun setNotiViewBackgroundCircleImageView(view: CircleImageView, value: Int?) {
        value?.let {
            val context = view.context
            var color = 0
            when (it) {
                // 0 is unopened
                // 1 is opened
                0 -> { color = ContextCompat.getColor(context, R.color.primaryDarkColor) }
                1 -> { color = ContextCompat.getColor(context, R.color.primaryColor) }
            }
            view.circleBackgroundColor = color
        }
    }

    @JvmStatic
    @BindingAdapter("app:loadTypeText")
    fun setLoadTypeSettingsText(view: TextView, value: Int?) {
        value?.let {
            val context = view.context
            val color: Int
            val text: String
            // 1 explosive
            // 2 flammable gas
            // 3 non-flam non-tox gas
            // 4 toxic gas
            // 5 flammable liquid
            // 6 flammable solid
            // 7 spontaneously combustible
            // 8 dangerous when wet
            // 9 oxidizing agent
            // 10 organic peroxide
            // 11 toxic
            // 12 infectious substance
            // 13 radioactive
            // 14 corrosive
            // 15 misc. dangerous goods
            // 16-35 non-hazardous
            when (it) {
                1 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_explosive)
                }
                2 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_flammable_gas)
                }
                3 -> {
                    color = ContextCompat.getColor(context, R.color.green)
                    text = context.getString(R.string.placard_non_flam_non_tox_gas)
                }
                4 -> {
                    color = ContextCompat.getColor(context, R.color.yellow)
                    text = context.getString(R.string.placard_toxic_gas)
                }
                5 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_flammable_liquid)
                }
                6 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_flammable_solid)
                }
                7 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_spont_combust)
                }
                8 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_dang_wet)
                }
                9 -> {
                    color = ContextCompat.getColor(context, R.color.yellow)
                    text = context.getString(R.string.placard_oxi_agent)
                }
                10 -> {
                    color = ContextCompat.getColor(context, R.color.green)
                    text = context.getString(R.string.placard_organ_perox)
                }
                11 -> {
                    color = ContextCompat.getColor(context, R.color.yellow)
                    text = context.getString(R.string.placard_toxic)
                }
                12 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_infec_sub)
                }
                13 -> {
                    color = ContextCompat.getColor(context, R.color.yellow)
                    text = context.getString(R.string.placard_radioactive)
                }
                14 -> {
                    color = ContextCompat.getColor(context, R.color.yellow)
                    text = context.getString(R.string.placard_corrosive)
                }
                15 -> {
                    color = ContextCompat.getColor(context, R.color.red)
                    text = context.getString(R.string.placard_misc_hazard)
                }
                else -> {
                    color = ContextCompat.getColor(context, R.color.white)
                    text = context.getString(R.string.placard_non_hazardous)
                }
            }
            view.setTextColor(color)
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:loadTypeImage")
    fun setLoadTypeSettingsImage(view: ImageView, value: Int?) {
        value?.let {
            val context = view.context
            var drawable : Drawable? = null
            // 1 explosive
            // 2 flammable gas
            // 3 non-flam non-tox gas
            // 4 toxic gas
            // 5 flammable liquid
            // 6 flammable solid
            // 7 spontaneously combustible
            // 8 dangerous when wet
            // 9 oxidizing agent
            // 10 organic peroxide
            // 11 toxic
            // 12 infectious substance
            // 13 radioactive
            // 14 corrosive
            // 15 misc. dangerous goods
            // 16-35 non-hazardous
            when (it) {
                1 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_1_1_explosives) }
                2 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_2_flammable_gas) }
                3 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_2_non_flammable_gas) }
                4 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_6_toxic) }
                5 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_3_flammable) }
                6 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_4_flammable_solid) }
                7 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_4_spontaneously_combustible) }
                8 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_4_dangerous_when_wet) }
                9 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_5_1_oxidizer) }
                10 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_5_2_organic_peroxide) }
                11 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_6_toxic) }
                12 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_6_inhalation_hazard) }
                13 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_7_radioactive) }
                14 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_8_corrosive) }
                15 -> { drawable = ContextCompat.getDrawable(context, R.drawable.placard_0_dangerous) }
                //else -> {  }
            }
            drawable?.let { newDrawable ->
                view.setImageDrawable(newDrawable)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("app:setLoadWeight")
    fun setLoadWeightText(view: TextView, value: Double?) {
        value?.let {
            val context = view.context
            val text : String = value.toString() + context.getString(R.string.pounds_text)
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:setTrailerType")
    fun setTrailerType(view: TextView, value: Int?) {
        value?.let {
            val context = view.context
            val text = when (it) {
                // 1 flatbed
                // 2 step deck
                // 3 reefer
                // 4 auto carrier
                // 5 dump trailer
                // 6 tanker
                // 7 LTL Dry van
                // 8 Partial Dry van
                // 9-15 dry van
                1 -> { context.getString(R.string.trailer_flatbed) }
                2 -> { context.getString(R.string.trailer_step_deck) }
                3 -> { context.getString(R.string.trailer_reefer) }
                4 -> { context.getString(R.string.trailer_auto_carrier) }
                5 -> { context.getString(R.string.trailer_dump) }
                6 -> { context.getString(R.string.trailer_tanker) }
                7 -> { context.getString(R.string.trailer_ltl_dry_van) }
                8 -> { context.getString(R.string.trailer_partial_dry_van) }
                else -> { context.getString(R.string.trailer_dry_van) }
            }
            view.text = text
        }
    }

    @JvmStatic
    @BindingAdapter("app:loadPay")
    fun setLoadPayText(view: TextView, value: Double?) {
        value?.let {
            val context = view.context
            val text = "$$value/mi"
            val color = when (it) {
                in(0.00..2.00) -> { ContextCompat.getColor(context, R.color.red) }
                in(2.00..2.75) -> { ContextCompat.getColor(context, R.color.yellow) }
                else -> { ContextCompat.getColor(context, R.color.green) }
            }
            view.setTextColor(color)
            view.text = text
        }
    }





}