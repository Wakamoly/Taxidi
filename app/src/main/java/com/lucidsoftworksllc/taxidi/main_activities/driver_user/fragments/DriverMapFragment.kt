package com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApiRequest
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverMapFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.driver_simulator.Simulator
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapsView
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMapViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters.CompanyMarkerCustomInfoWindow
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import com.lucidsoftworksllc.taxidi.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.ArrayList

class DriverMapFragment : BaseFragment<DriverMapViewModel, DriverMapFragmentBinding, DriverMapRepository>(), DriverMapsView {

    private lateinit var googleMap: GoogleMap
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback
    private var currentLatLng: LatLng? = null
    private var pickUpLatLng: LatLng? = null
    private var dropLatLng: LatLng? = null
    private val nearbyCompanyMarkerList = arrayListOf<Marker>()
    private var destinationMarker: Marker? = null
    private var originMarker: Marker? = null
    private var greyPolyLine: Polyline? = null
    private var blackPolyline: Polyline? = null
    private var previousLatLngFromServer: LatLng? = null
    private var currentLatLngFromServer: LatLng? = null
    private var movingCompanyMarker: Marker? = null
    private var reInitNearby = false
    private var companyMapMarkerModel : CompanyMapMarkerModel? = null

    companion object {
        private const val TAG = "MapsActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        private const val PICKUP_REQUEST_CODE = 1
        private const val DROP_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(getString(R.string.map_fragment_name))
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val mapFragment = childFragmentManager.findFragmentById(R.id.driver_map) as SupportMapFragment
        mapFragment.getMapAsync(callback)
        setUpClickListener()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, {
            when (it) {
                1 -> {
                    informCompanyBooked()
                }
                2 -> {
                    showPath(viewModel.pickUpPath.value?.toList())
                }
                3 -> {
                    //updateCompanyLocation(viewModel.companyLocation.value)
                }
                4 -> {
                    informDriverIsArriving()
                }
                5 -> {
                    informDriverArrived()
                }
                6 -> {
                    informTripStart()
                }
                7 -> {
                    informTripEnd()
                }
                8 -> {
                    showRoutesNotAvailableError()
                }
                9 -> {
                    showDirectionApiFailedError(viewModel.directionApiFailedError.value)
                }
                10 -> {
                    showNearbyCompanies(viewModel.nearbyCompanies.value)
                }
                11 -> {
                    // Show path and show layout for accept/deny
                    showSampleTripPath(viewModel.sampleTripPath.value)
                }
            }
        })
    }

    private fun showSampleTripPath(values: ArrayList<LatLng>?) {
        values?.let {
            showPath(values)
        }
    }

    private fun setUpClickListener() {
        binding.tripDismissButton.setOnClickListener {
            binding.tripDetailAccessWindowMotion.transitionToStart()
        }
        binding.tripDetailsButton.setOnClickListener {
            viewModel.navigateToShipmentDetails(companyMapMarkerModel)
        }
        /*pickUpTextView.setOnClickListener {
            launchLocationAutoCompleteActivity(PICKUP_REQUEST_CODE)
        }
        dropTextView.setOnClickListener {
            launchLocationAutoCompleteActivity(DROP_REQUEST_CODE)
        }
        requestCabButton.setOnClickListener {
            statusTextView.visibility = View.VISIBLE
            statusTextView.text = getString(R.string.requesting_your_cab)
            requestCabButton.isEnabled = false
            pickUpTextView.isEnabled = false
            dropTextView.isEnabled = false
            presenter.requestCab(pickUpLatLng!!, dropLatLng!!)
        }
        nextRideButton.setOnClickListener {
            reset()
        }*/
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCarMarkerAndGet(company: CompanyMapMarkerModel): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(
            MapUtils.getBusinessBitmap(
                requireContext()
            )
        )
        return googleMap.addMarker(
            MarkerOptions().position(company.latLng).flat(true).icon(bitmapDescriptor)
        ).apply { tag = company }
    }

    private fun addOriginDestinationMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getDestinationBitmap())
        return googleMap.addMarker(
            MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor)
        )
    }

    private fun setCurrentLocationAsPickUp() {
        pickUpLatLng = currentLatLng
        //pickUpTextView.text = getString(R.string.current_location)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocationOnMap() {
        googleMap.setPadding(0, ViewUtils.dpToPx(48f), 0, 0)
        googleMap.isMyLocationEnabled = true
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        // for getting the current location update after every 2 seconds
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (currentLatLng == null) {
                    val location = locationResult.locations.first()
                    currentLatLng = LatLng(location.latitude, location.longitude)
                    setCurrentLocationAsPickUp()
                    enableMyLocationOnMap()
                    moveCamera(currentLatLng)
                    animateCamera(currentLatLng)
                    if (viewModel.nearbyCompanies.value != null && viewModel.nearbyCompanies.value!!.isNotEmpty()){
                        showNearbyCompanies(viewModel.nearbyCompanies.value)
                    } else {
                        viewModel.requestNearbyCompanies(currentLatLng!!)
                    }
                    /*for (location in locationResult.locations) {
                        if (currentLatLng == null) {

                        }
                    }*/
                }
                // Few more things we can do here:
                // For example: Update the location of user on server
            }
        }
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun checkAndShowRequestButton() {
        if (pickUpLatLng !== null && dropLatLng !== null) {
            //requestCabButton.visibility = View.VISIBLE
            //requestCabButton.isEnabled = true
        }
    }

    private fun reset() {
        //statusTextView.visibility = View.GONE
        //nextRideButton.visibility = View.GONE
        nearbyCompanyMarkerList.forEach { it.remove() }
        nearbyCompanyMarkerList.clear()
        previousLatLngFromServer = null
        currentLatLngFromServer = null
        if (currentLatLng != null) {
            moveCamera(currentLatLng)
            animateCamera(currentLatLng)
            setCurrentLocationAsPickUp()
            viewModel.requestNearbyCompanies(currentLatLng!!)
        } else {
            //pickUpTextView.text = ""
        }
        //pickUpTextView.isEnabled = true
        //dropTextView.isEnabled = true
        //dropTextView.text = ""
        movingCompanyMarker?.remove()
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
        dropLatLng = null
        greyPolyLine = null
        blackPolyline = null
        originMarker = null
        destinationMarker = null
        movingCompanyMarker = null
    }

    override fun onStart() {
        super.onStart()
        if (currentLatLng == null) {
            when {
                PermissionUtils.isAccessFineLocationGranted(requireContext()) -> {
                    when {
                        PermissionUtils.isLocationEnabled(requireContext()) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(requireContext())
                        }
                    }
                }
                else -> {
                    PermissionUtils.requestAccessFineLocationPermission(
                        requireActivity(),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(requireContext()) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(requireContext())
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        val companyInfoWindow = CompanyMarkerCustomInfoWindow(requireContext())
        googleMap.setInfoWindowAdapter(companyInfoWindow)
        if (reInitNearby) {
            showNearbyCompanies(viewModel.nearbyCompanies.value)
        }
        googleMap.setOnInfoWindowClickListener(infoWindowClickCallback)
    }

    private val infoWindowClickCallback = GoogleMap.OnInfoWindowClickListener { marker ->
        val infoWindowData: CompanyMapMarkerModel? = marker.tag as CompanyMapMarkerModel?
        infoWindowData?.let {
            // Reset the selected marker, then set it again
            companyMapMarkerModel = null
            companyMapMarkerModel = it
            val origin = infoWindowData.latLng
            val destination = infoWindowData.toLatLng
            viewModel.getRoute(origin, destination)
            binding.tripDetailAccessWindowMotion.transitionToEnd()
            /*if (currentLatLng != null){

            } else {
                showPath(listOf(infoWindowData.latLng, infoWindowData.toLatLng))
            }*/
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun showNearbyCompanies(companies: List<CompanyMapMarkerModel>?) {
        companies?.let {
            nearbyCompanyMarkerList.clear()
            if (::googleMap.isInitialized){
                for (company in companies) {
                    val nearbyCompanyMarker = addCarMarkerAndGet(company)
                    nearbyCompanyMarkerList.add(nearbyCompanyMarker)
                }
            } else {
                reInitNearby = true
            }
        }
    }

    override fun informCompanyBooked() {
        nearbyCompanyMarkerList.forEach { it.remove() }
        nearbyCompanyMarkerList.clear()
        //requestCabButton.visibility = View.GONE
        viewModel.showSnackBarInt.value = R.string.your_pickup_is_booked
    }

    override fun showPath(latLngList: List<LatLng>?) {
        latLngList?.let {
            resetMarkers()
            val builder = LatLngBounds.Builder()
            for (latLng in latLngList) {
                builder.include(latLng)
            }
            val bounds = builder.build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
            PolylineOptions().color(Color.GRAY).width(5f).addAll(latLngList).apply {
                greyPolyLine = googleMap.addPolyline(this)
            }

            PolylineOptions().width(5f).color(Color.BLACK).apply {
                googleMap.addPolyline(this)
            }

            originMarker = addOriginDestinationMarkerAndGet(latLngList[0]).apply {
                setAnchor(0.5f, 0.5f)
            }
            destinationMarker = addOriginDestinationMarkerAndGet(latLngList[latLngList.size - 1]).apply {
                setAnchor(0.5f, 0.5f)
            }

            AnimationUtils.polyLineAnimator().apply {
                addUpdateListener { valueAnimator ->
                    val percentValue = (valueAnimator.animatedValue as Int)
                    val index = (greyPolyLine?.points!!.size * (percentValue / 100.0f)).toInt()
                    blackPolyline?.points = greyPolyLine?.points!!.subList(0, index)
                }
                start()
            }
        }
    }

    /*override fun updateCompanyLocation(latLng: LatLng?) {
        latLng?.let {
            if (movingCompanyMarker == null) {
                movingCompanyMarker = addCarMarkerAndGet(latLng)
            }
            if (previousLatLngFromServer == null) {
                currentLatLngFromServer = latLng
                previousLatLngFromServer = currentLatLngFromServer
                movingCompanyMarker?.position = currentLatLngFromServer
                movingCompanyMarker?.setAnchor(0.5f, 0.5f)
                animateCamera(currentLatLngFromServer)
            } else {
                previousLatLngFromServer = currentLatLngFromServer
                currentLatLngFromServer = latLng
                val valueAnimator = AnimationUtils.truckAnimator()
                valueAnimator.addUpdateListener { va ->
                    if (currentLatLngFromServer != null && previousLatLngFromServer != null) {
                        val multiplier = va.animatedFraction
                        val nextLocation = LatLng(
                            multiplier * currentLatLngFromServer!!.latitude + (1 - multiplier) * previousLatLngFromServer!!.latitude,
                            multiplier * currentLatLngFromServer!!.longitude + (1 - multiplier) * previousLatLngFromServer!!.longitude
                        )
                        movingCompanyMarker?.position = nextLocation
                        movingCompanyMarker?.setAnchor(0.5f, 0.5f)
                        val rotation = MapUtils.getRotation(
                            previousLatLngFromServer!!,
                            nextLocation
                        )
                        if (!rotation.isNaN()) {
                            movingCompanyMarker?.rotation = rotation
                        }
                        animateCamera(nextLocation)
                    }
                }
                valueAnimator.start()
            }
        }
    }*/

    override fun informDriverIsArriving() {
        viewModel.showSnackBarInt.value = R.string.your_driver_is_arriving
    }

    override fun informDriverArrived() {
        viewModel.showSnackBarInt.value = R.string.your_pickup_has_arrived
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
    }

    override fun informTripStart() {
        viewModel.showSnackBarInt.value = R.string.your_pickup_is_on_route_to_dest
        previousLatLngFromServer = null
    }

    override fun informTripEnd() {
        viewModel.showSnackBarInt.value = R.string.route_end
        //nextRideButton.visibility = View.VISIBLE
        resetMarkers()
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
    }

    private fun resetMarkers() {
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
    }

    override fun showRoutesNotAvailableError() {
        viewModel.showSnackBarInt.value = R.string.route_not_available_most_likely_in_the_ocean
        //reset()
    }

    override fun showDirectionApiFailedError(error: String?) {
        Log.d(TAG, "showDirectionApiFailedError: error: $error")
        requireContext().toastLong(error ?: getString(R.string.srverr_unknown))
        reset()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(::googleMap.isInitialized){
            when (item.itemId) {
                // Change the map type based on the user's selection.
                R.id.normal_map -> {
                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL; return true
                }
                R.id.hybrid_map -> {
                    googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID; return true
                }
                R.id.satellite_map -> {
                    googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE; return true
                }
                R.id.terrain_map -> {
                    googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN; return true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
        return false
    }

    /*private fun getLocationPermission() {
        *//*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         *//*
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
    }*/

    /*override fun onRequestPermissionsResult(
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
    }*/

    /*private fun updateLocationUI() {
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
    }*/

    /*private fun getDeviceLocation() {
        *//*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         *//*
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
    }*/

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


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = DriverMapFragmentBinding.inflate(inflater, container, false)
    override fun getViewModel() = DriverMapViewModel::class.java
    override fun getFragmentRepository() = DriverMapRepository(
        userPreferences,
        remoteDataSource.buildApi(DriverMapAPI::class.java, fcmToken)
    )

}