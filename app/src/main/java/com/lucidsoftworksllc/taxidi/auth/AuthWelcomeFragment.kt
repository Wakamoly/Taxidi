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
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.findNavController
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.databinding.FragmentAuthWelcomeBinding
import kotlin.random.Random

class AuthWelcomeFragment : Fragment() {

    private lateinit var viewGroup: RelativeLayout
    private lateinit var binding: FragmentAuthWelcomeBinding
    private lateinit var motionLayout: MotionLayout
    private var stopTrucks: Boolean = false
    private var numTrucks = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        // Inflate view and obtain an instance of the binding class.
        binding = FragmentAuthWelcomeBinding.inflate(inflater,container,false)
        viewGroup = binding.authWelcomeRelativeLayout
        motionLayout = binding.authWelcomeMotionLayout
        setupOnClicks()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTrucks(20)
    }

    override fun onStart() {
        super.onStart()
        stopTrucks = false
    }

    override fun onStop() {
        super.onStop()
        stopTrucks = true
    }

    private fun setupOnClicks() {
        binding.loginAsCompany.setOnClickListener {
            requireView().findNavController().navigate(AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToAuthSignInFragment("company"))
        }
        binding.loginAsDriver.setOnClickListener {
            requireView().findNavController().navigate(AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToAuthSignInFragment("driver"))
        }
    }

    private fun startTrucks(trucks: Int) {
        var i = 1
        if (!stopTrucks){
            while (i <= trucks && numTrucks <= 20){
                i++
                numTrucks++
                // Create a new truck view in a random X position above the container.

                // Local variables we'll need in the code below
                val containerW = viewGroup.width
                val containerH = viewGroup.height
                var truckW = 5f
                var truckH = 5f

                // Create the new truck (an ImageView holding our drawable) and add it to the container
                val newTruck = AppCompatImageView(requireContext())

                // TODO: 1/25/2021 Most likely just going to stick with a semi/tractor or box truck for MVP, but this is fun
                when((1..2).random()){
                    1 -> newTruck.setImageResource(R.drawable.ic_baseline_local_shipping_64)
                    2 -> newTruck.setImageResource(R.drawable.ic_baseline_shuttle_64)
                }

                newTruck.layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)

                viewGroup.addView(newTruck)

                // Position the view at a random place between the left and right edges of the container
                newTruck.translationX = Math.random().toFloat() * containerW - truckW
                newTruck.translationY = Math.random().toFloat() * containerH - truckH

                // TODO: 1/22/2021 Fix animation imageview start! Views seem to break once added the first time, then somehow are fixed when re-added

                // Scale the view randomly between 10-160% of its default size
                newTruck.scaleX = Math.random().toFloat() * 1.5f + .1f
                newTruck.scaleY = newTruck.scaleX
                truckW *= newTruck.scaleX
                truckH *= newTruck.scaleY
                newTruck.rotation = 50f

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
                        numTrucks--
                        startTrucks(2)
                    }
                })

                // Start the animation
                set.start()
            }
        }
    }

}