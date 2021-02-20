package com.lucidsoftworksllc.taxidi.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.databinding.FragmentAuthWelcomeBinding
import com.lucidsoftworksllc.taxidi.utils.Extensions.toastShort
import java.util.*

class AuthWelcomeFragment : Fragment() {

    private lateinit var viewGroup: RelativeLayout
    private lateinit var binding: FragmentAuthWelcomeBinding
    private lateinit var motionLayout: MotionLayout
    private var stopTrucks: Boolean = false
    private var numTrucks = 1

    companion object {
        private const val TAG = "AuthWelcomeFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        (activity as AppCompatActivity).supportActionBar?.hide()
        // Inflate view and obtain an instance of the binding class.
        binding = FragmentAuthWelcomeBinding.inflate(inflater, container, false)
        viewGroup = binding.authWelcomeRelativeLayout
        motionLayout = binding.authWelcomeMotionLayout
        binding.lifecycleOwner = this
        setupOnClicks()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        stopTrucks = false
        startTrucks(2)
    }

    override fun onStop() {
        super.onStop()
        stopTrucks = true
    }

    private fun setupOnClicks() {
        binding.loginAsCompany.setOnClickListener {
            // TODO: 2/1/2021 Fix business registration
            requireContext().toastShort("Company login/register not implemented yet")
            //requireView().findNavController().navigate(AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToAuthSignInFragment("company"))
        }
        binding.loginAsDriver.setOnClickListener {
            requireView().findNavController().navigate(AuthWelcomeFragmentDirections.actionAuthWelcomeFragmentToAuthSignInFragment("driver"))
        }
    }

    private fun startTrucks(trucks: Int) {
        // Local variables we'll need in the code below
        val containerW = viewGroup.width
        val containerH = viewGroup.height
        if (containerH > 0 && containerW > 0){
            var truckW = 5f
            var truckH = 5f
            var i = 1
            if (!stopTrucks){
                while (i <= trucks && numTrucks <= 20){
                    i++
                    numTrucks++

                    // Create the new truck (an ImageView holding our drawable)
                    val newTruck = AppCompatImageView(requireContext())

                    // TODO: 1/25/2021 Most likely just going to stick with a semi/tractor or box truck for MVP, but this is fun
                    when((1..2).random()){
                        1 -> newTruck.setImageResource(R.drawable.ic_baseline_local_shipping_64)
                        2 -> newTruck.setImageResource(R.drawable.ic_baseline_shuttle_64)
                    }

                    newTruck.layoutParams = RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT)
                    viewGroup.clipChildren = false
                    viewGroup.clipToPadding = false

                    // Position the view at a random place between the left and top edges of the container
                    newTruck.translationX = (Math.random().toFloat() * containerW) - truckW
                    newTruck.translationY = (Math.random().toFloat() * containerH) - truckH

                    // Scale the view randomly
                    newTruck.scaleX = Math.random().toFloat() * 1.5f + .1f
                    newTruck.scaleY = newTruck.scaleX
                    truckW *= newTruck.scaleX
                    truckH *= newTruck.scaleY
                    newTruck.rotation = 50f

                    val path = Path().apply {
                        moveTo(newTruck.translationX - containerW, newTruck.translationY - containerH)
                        lineTo(newTruck.translationX + containerW, newTruck.translationY + containerH)
                    }

                    Handler(Looper.getMainLooper()).postDelayed({
                        ObjectAnimator.ofFloat(newTruck, View.TRANSLATION_X, View.TRANSLATION_Y, path).apply {
                            repeatCount = ObjectAnimator.INFINITE
                            repeatMode = ObjectAnimator.RESTART
                            interpolator = LinearInterpolator()
                            duration = (Math.random() * 10000 + 500).toLong()
                            addListener(object : Animator.AnimatorListener {
                                override fun onAnimationStart(animation: Animator?) {
                                    viewGroup.addView(newTruck)
                                    startTrucks(2)
                                }
                                override fun onAnimationEnd(animation: Animator?) { numTrucks-- }
                                override fun onAnimationCancel(animation: Animator?) { }
                                override fun onAnimationRepeat(animation: Animator?) {
                                    if (stopTrucks) {
                                        animation?.cancel()
                                    }
                                }
                            })
                            start()
                        }
                    }, 1000)

                }
            }
        } else {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ startTrucks(trucks) }, 100)
            Log.d(TAG, "startTrucks: delaying trucks")
        }

    }

}