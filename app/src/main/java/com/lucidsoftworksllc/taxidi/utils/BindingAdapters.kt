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
                    // transition crossfade has some strange bugs with other animations
                    //.transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(view)
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



}