package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverMapFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMapViewModel
import com.lucidsoftworksllc.taxidi.utils.fcmToken
import com.lucidsoftworksllc.taxidi.utils.setTitle

class DriverMapFragment : BaseFragment<DriverMapViewModel, DriverMapFragmentBinding, DriverMapRepository>() {

    private var locationPermissionGranted = false
    private var lastKnownLocation : Location? = null
    private var cameraPosition : CameraPosition? = null
    private val defaultLocation : LatLng = LatLng(-34.0, 151.0)
    private val defaultZoom = 15f
    private var marker: Marker? = null
    private lateinit var map: GoogleMap
    //private lateinit var remindersDao: RemindersDao

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        setMapStyle(map)
        initObservers()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()
        if (cameraPosition != null) {
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                cameraPosition?.target, cameraPosition?.zoom!!
            ))
        } else {
            // Get the current location of the device and set the position of the map.
            getDeviceLocation()
        }

    }

    companion object {
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val TAG = "SelectLocationFrag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //remindersDao = LocalDB.createRemindersDao(req)
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // TODO: 2/5/2021
        //    kotlin.UninitializedPropertyAccessException: lateinit property map has not been initialized -> after loaded this fragment, then screen rotated a few times.
        map.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.driver_map) as SupportMapFragment
        mapFragment.getMapAsync(callback)

        setTitle(getString(R.string.map_fragment_name))
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun initObservers() {
        // TODO: 2/2/2021 Load companies here
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(::map.isInitialized){
            when (item.itemId) {
                // Change the map type based on the user's selection.
                R.id.normal_map -> { map.mapType = GoogleMap.MAP_TYPE_NORMAL; return true }
                R.id.hybrid_map -> { map.mapType = GoogleMap.MAP_TYPE_HYBRID; return true }
                R.id.satellite_map -> { map.mapType = GoogleMap.MAP_TYPE_SATELLITE; return true }
                R.id.terrain_map -> { map.mapType = GoogleMap.MAP_TYPE_TERRAIN; return true }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DriverMapFragmentBinding.inflate(inflater, container, false)

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(mCtx.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
            updateLocationUI()
            getDeviceLocation()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                map.isMyLocationEnabled = true
                map.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map.isMyLocationEnabled = false
                map.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = FusedLocationProviderClient(mCtx).lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation?.latitude ?: defaultLocation.latitude,
                                    lastKnownLocation?.longitude ?: defaultLocation.longitude), defaultZoom
                            ))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map.moveCamera(
                            CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, defaultZoom))
                        map.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
            map.moveCamera(
                CameraUpdateFactory
                .newLatLngZoom(defaultLocation, defaultZoom))
            map.uiSettings?.isMyLocationButtonEnabled = false
        }
    }

    /**
     *
     * Navigate to https://mapstyle.withgoogle.com/ in your browser to style the map.
     *
     */
    private fun setMapStyle(map: GoogleMap) {
        try {
            // Customize the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    mCtx,
                    R.raw.map_style
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    override fun getViewModel() = DriverMapViewModel::class.java
    override fun getFragmentRepository() = DriverMapRepository(
        userPreferences,
        remoteDataSource.buildApi(DriverMapAPI::class.java, fcmToken)
    )

}