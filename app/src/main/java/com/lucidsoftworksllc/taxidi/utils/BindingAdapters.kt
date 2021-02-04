package com.lucidsoftworksllc.taxidi.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter


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
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        Log.d("BINDING ADAPTERS", "setFadeVisible: CALLED")
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
    @BindingAdapter("app:loadImage")
    fun setImageResource(view: ImageView, imageUrl: String?) {
        val context = view.context
        //val options = RequestOptions()
          //  .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        imageUrl?.let {
            val finalImageUrl = Constants.BASE_URL + imageUrl
            Glide.with(context)
                //.setDefaultRequestOptions(options)
                .load(finalImageUrl)
                .transition(DrawableTransitionOptions.withCrossFade(500))
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
                // 3 is active
                0 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_offline_24) }
                1 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_online_idle_24) }
                2 -> { drawable = ContextCompat.getDrawable(context, R.drawable.ic_online_active_24) }
            }
            view.setImageDrawable(drawable)
        }
    }



}