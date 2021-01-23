package com.lucidsoftworksllc.taxidi.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.databinding.FragmentAuthWelcomeBinding

class AuthWelcomeFragment : Fragment() {

    private lateinit var viewGroup: ViewGroup
    private lateinit var binding: FragmentAuthWelcomeBinding
    private lateinit var motionLayout: MotionLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        // Inflate view and obtain an instance of the binding class.
        binding = FragmentAuthWelcomeBinding.inflate(inflater,container,false)
        viewGroup = binding.authWelcomeFrameLayout
        motionLayout = binding.authWelcomeMotionLayout
        startTrucks(20)
        setupOnClicks()
        return binding.root
    }

    private fun setupOnClicks() {
        binding.loginAsCompany.setOnClickListener {
            motionLayout.setTransition(R.id.click_to_login_start, R.id.click_to_login_end)
            motionLayout.transitionToEnd()
        }
        binding.loginAsDriver.setOnClickListener {
            motionLayout.setTransition(R.id.click_to_login_start, R.id.click_to_login_end)
            motionLayout.transitionToEnd()
        }
    }

    private fun startTrucks(trucks: Int) {
        var i = 1
        while (i <= trucks){
            i++
            // Create a new truck view in a random X position above the container.

            // Local variables we'll need in the code below
            val containerW = viewGroup.width
            val containerH = viewGroup.height
            var truckW = 5f
            var truckH = 5f

            // Create the new truck (an ImageView holding our drawable) and add it to the container
            val newTruck = AppCompatImageView(requireContext())
            newTruck.setImageResource(R.drawable.ic_baseline_local_shipping_64)
            newTruck.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT)
            viewGroup.addView(newTruck)

            // TODO: 1/22/2021 Fix animation imageview start!

            // Scale the view randomly between 10-160% of its default size
            newTruck.scaleX = Math.random().toFloat() * 1.5f + .1f
            newTruck.scaleY = newTruck.scaleX
            truckW *= newTruck.scaleX
            truckH *= newTruck.scaleY

            // Position the view at a random place between the left and right edges of the container
            newTruck.translationX = Math.random().toFloat() * containerW - truckW
            newTruck.translationY = Math.random().toFloat() * containerH - truckH
            newTruck.rotation = 45f

            // Create an animator that moves the view from a starting position right about the container
            // to an ending position right below the container. Set an accelerate interpolator to give
            // it a gravity/falling feel
            val path = Path().apply {
                moveTo(newTruck.translationX-containerW, newTruck.translationY-containerH)
                lineTo(newTruck.translationX+containerW, newTruck.translationY+containerH)
            }

            val mover = ObjectAnimator.ofFloat(newTruck, View.TRANSLATION_X, View.TRANSLATION_Y, path)
            mover.interpolator = LinearInterpolator()

            // Use an AnimatorSet to play the falling and rotating animators in parallel for a duration
            // of a half-second to two seconds
            val set = AnimatorSet()
            set.play(mover)
            set.duration = (Math.random() * 7500 + 500).toLong()

            // When the animation is done, remove the created view from the container
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    viewGroup.removeView(newTruck)
                    startTrucks(1)
                }
            })

            // Start the animation
            set.start()
        }

    }

}