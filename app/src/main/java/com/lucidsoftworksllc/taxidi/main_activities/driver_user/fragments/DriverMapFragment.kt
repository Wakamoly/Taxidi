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
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.databinding.DriverMapFragmentBinding
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMapRepository
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapAPI
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMapsView
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.view_models.DriverMapViewModel
import com.lucidsoftworksllc.taxidi.main_activities.driver_user.list_adapters.CompanyMarkerCustomInfoWindow
import com.lucidsoftworksllc.taxidi.others.models.server_responses.CompanyMapMarkerModel
import com.lucidsoftworksllc.taxidi.utils.*
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
    private var movingDriverMarker: Marker? = null
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
                    updateSimulatedDriverLocation(viewModel.driverLocation.value)
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
            //viewModel.navigateToShipmentDetails(companyMapMarkerModel)
            // TODO: 2/15/2021 FIX
            if (companyMapMarkerModel != null) {
                binding.tripDetailAccessWindowMotion.transitionToStart()
                viewModel.requestCompany(companyMapMarkerModel!!.latLng, companyMapMarkerModel!!.toLatLng)
            } else {
                requireView().snackbar("Map marker info null!")
            }
        }
    }

    private fun moveCamera(latLng: LatLng?) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun animateCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun centerCamera(latLng: LatLng?) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(15.5f).build()
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun addCompanyMarkerAndGet(company: CompanyMapMarkerModel): Marker {
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
        movingDriverMarker?.remove()
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
        dropLatLng = null
        greyPolyLine = null
        blackPolyline = null
        originMarker = null
        destinationMarker = null
        movingDriverMarker = null
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
                    val nearbyCompanyMarker = addCompanyMarkerAndGet(company)
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

    private fun addTruckMarkerAndGet(latLng: LatLng): Marker {
        val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(MapUtils.getTruckBitmap(requireContext()))
        return googleMap.addMarker(MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor))
    }

    override fun updateSimulatedDriverLocation(latLng: LatLng?) {
        latLng?.let {
            if (movingDriverMarker == null) {
                movingDriverMarker = addTruckMarkerAndGet(latLng)
            }
            if (previousLatLngFromServer == null) {
                currentLatLngFromServer = latLng
                previousLatLngFromServer = currentLatLngFromServer
                movingDriverMarker?.position = currentLatLngFromServer
                movingDriverMarker?.setAnchor(0.5f, 0.5f)
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
                        movingDriverMarker?.position = nextLocation
                        movingDriverMarker?.setAnchor(0.5f, 0.5f)
                        val rotation = MapUtils.getRotation(
                                previousLatLngFromServer!!,
                                nextLocation
                        )
                        if (!rotation.isNaN()) {
                            movingDriverMarker?.rotation = rotation
                        }
                        centerCamera(nextLocation)
                    }
                }
                valueAnimator.start()
            }
        }
    }

    override fun informDriverIsArriving() {
        viewModel.showSnackBarInt.value = R.string.you_are_arriving_at_pickup
    }

    override fun informDriverArrived() {
        viewModel.showSnackBarInt.value = R.string.you_have_arrived_at_pickup
        resetMarkers()
        // TODO: 2/15/2021 Generate pickup ticket
    }

    override fun informTripStart() {
        viewModel.showSnackBarInt.value = R.string.you_are_on_route_to_dropoff
        previousLatLngFromServer = null
    }

    override fun informTripEnd() {
        viewModel.showSnackBarInt.value = R.string.route_end_driver
        resetMarkers()
        // TODO: 2/15/2021 Generate dropoff ticket
    }

    private fun resetMarkers() {
        greyPolyLine?.remove()
        blackPolyline?.remove()
        originMarker?.remove()
        destinationMarker?.remove()
    }

    override fun showRoutesNotAvailableError() {
        viewModel.showSnackBarInt.value = R.string.route_not_available_most_likely_in_the_ocean
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