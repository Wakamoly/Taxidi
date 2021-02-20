package com.lucidsoftworksllc.taxidi.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.maps.model.LatLng
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseFragment
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.Extensions.getServerResponseInt
import kotlinx.coroutines.runBlocking
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.roundToInt

object Extensions {

    /**
     * Extension function to setup the RecyclerView
     */
    fun <T> RecyclerView.setup(
        adapter: BaseRecyclerViewAdapter<T>
    ) {
        this.apply {
            layoutManager = LinearLayoutManager(this.context)
            this.adapter = adapter
        }
    }

    fun Fragment.setTitle(title: String) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.title = title
        }
    }

    fun Fragment.setDisplayHomeAsUpEnabled(bool: Boolean) {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
                bool
            )
        }
    }

    //animate changing the view visibility
    fun View.fadeIn() {
        this.visibility = View.VISIBLE
        this.alpha = 0f
        this.animate().alpha(1f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeIn.alpha = 1f
            }
        })
    }

    //animate changing the view visibility
    fun View.fadeOut() {
        this.animate().alpha(0f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.alpha = 1f
                this@fadeOut.visibility = View.GONE
            }
        })
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    object GeofencingConstants {
        const val ACTION_GEOFENCE_EVENT = "ACTION_GEOFENCE_EVENT"
        const val GEOFENCE_RADIUS_METERS = 1609f
    }

    fun String.isEmailValid(): Boolean {
        // I've found the normal ways of checking an email don't allow more obscure email addresses that are actually valid.
        // This regex should cover near 100% of even the most obscure.
        return !TextUtils.isEmpty(this) && Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(this).matches()
    }

    fun Context.toastShort(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun Context.toastLong(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    fun <A : Activity> Activity.startNewActivity(activity: Class<A>){
        Intent(this, activity).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    fun View.visible(isVisible: Boolean){
        visibility = if(isVisible) View.VISIBLE else View.GONE
    }

    fun View.snackbar(message: String, actionText: String = "", action: (() -> Unit)? = null){
        if (Build.VERSION.SDK_INT >= 23) {
            val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
            action?.let {
                snackbar.setAction(actionText) {
                    it()
                }
            }
            snackbar.setActionTextColor(Color.parseColor("#45B431"))
            snackbar.setTextColor(Color.WHITE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#111111"))
            snackbar.show()
        }else{
            context?.toastLong(message)
        }
    }

    fun View.snackbarShort(message: String, actionText: String, action: (() -> Unit)? = null){
        if (Build.VERSION.SDK_INT >= 23) {
            val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
            action?.let {
                snackbar.setAction(actionText) {
                    it()
                }
            }
            snackbar.setActionTextColor(Color.parseColor("#45B431"))
            snackbar.setTextColor(Color.WHITE)
            val snackbarView = snackbar.view
            snackbarView.setBackgroundColor(Color.parseColor("#111111"))
            snackbar.show()
        }else{
            context?.toastShort(message)
        }
    }

    fun Fragment.startBaseObservables(viewModel: BaseViewModel){
        viewModel.showErrorMessage.observe(this, {
            this.activity?.toastShort(it)
        })
        viewModel.showToast.observe(this, {
            this.activity?.toastShort(it)
        })
        viewModel.showSnackBar.observe(this, {
            this.view?.snackbar(it, "")
        })
        viewModel.showSnackBarInt.observe(this, {
            this.view?.snackbar(getString(it), "")
        })
        viewModel.navigationCommand.observe(this, { command ->
            when (command) {
                is NavigationCommand.To -> findNavController().navigate(command.directions)
                is NavigationCommand.Back -> findNavController().popBackStack()
                is NavigationCommand.BackTo -> findNavController().popBackStack(
                    command.destinationId,
                    false
                )
            }
        })
        viewModel.userAuthExpired.observe(this, {
            if (it) {
                (this as BaseFragment<*,*,*>).logout()
            }
        })
    }

    /**
     * Datastore references
     * TODO 2/4/21 Remove runBlocking calls, find a better way to grab these values.
     */

    val Fragment.fcmToken: String
        get() {
            var storedFcmToken: String
            runBlocking {
                storedFcmToken = UserPreferences(requireContext()).fCMToken()
            }
            Log.d("Extensions", "fcmToken Fragment: $storedFcmToken")
            return storedFcmToken
        }

    val Fragment.authToken: String
        get() {
            var authToken: String
            runBlocking {
                authToken = UserPreferences(requireContext()).authToken()
            }
            Log.d("Extensions", "authToken Fragment: $authToken")
            return authToken
        }

    val Fragment.deviceUsername: String
        get() {
            var username: String
            runBlocking {
                username = UserPreferences(requireContext()).userUsername()
            }
            Log.d("Extensions", "username Fragment: $username")
            return username
        }

    val Fragment.deviceUserID: Int
        get() {
            var userID: Int
            runBlocking {
                userID = UserPreferences(requireContext()).userID()
            }
            Log.d("Extensions", "deviceUserID Fragment: $userID")
            return userID
        }

    val Activity.getFcmToken: String
        get() {
            var storedFcmToken: String
            runBlocking {
                storedFcmToken = UserPreferences(this@getFcmToken).fCMToken()
            }
            Log.d("Extensions", "fcmToken: $storedFcmToken")
            return storedFcmToken
        }

    val Activity.getAuthToken: String
        get() {
            var authToken: String
            runBlocking {
                authToken = UserPreferences(this@getAuthToken).authToken()
            }
            Log.d("Extensions", "authToken: $authToken")
            return authToken
        }

val Activity.getSignedInAs: String
    get() {
        var signedInAs: String
        runBlocking {
            signedInAs = UserPreferences(this@getSignedInAs).userType()
        }
        Log.d("Extensions", "signedInAs: $signedInAs")
        return signedInAs
    }

val Activity.getIsUserLoggedIn: Boolean
    get() {
        var isUserLoggedIn: Boolean
        runBlocking {
            isUserLoggedIn = UserPreferences(this@getIsUserLoggedIn).isUserLoggedIn()
        }
        Log.d("Extensions", "isUserLoggedIn: $isUserLoggedIn")
        return isUserLoggedIn
    }

    fun String.getServerResponseInt(): Int {
        // Server responses coded 0xxx for Success values and 1xxx for failures.
        // This is done instead of receiving a plain english response string from the server. A code is given, then decoded here for locality
        // Been around the block with this before, I'm liking this solution.
        return when(this) {
            // Success
            "0001" -> R.string.srvsuc_register
            "0002" -> R.string.srvsuc_login
            "0003" -> R.string.srvsuc_load_profile
            "0004" -> R.string.srvsuc_load_notifications
            "0005" -> R.string.srvsuc_upload_fcm
            "0006" -> R.string.srvsuc_home

            // Failure
            "1001" -> R.string.srverr_generic
            "1002" -> R.string.srverr_invalid_pass
            "1003" -> R.string.srverr_invalid_email
            "1004" -> R.string.srverr_invalid_username
            "1005" -> R.string.srverr_username_or_email_taken
            "1006" -> R.string.srverr_ip_banned_lol
            "1007" -> R.string.srverr_invalid_username2
            "1008" -> R.string.srverr_username_or_email_taken
            "1009" -> R.string.srverr_email_not_in_use
            "1010" -> R.string.srverr_login_failure
            "1011" -> R.string.srverr_profile_id
            "1012" -> R.string.srverr_load_profile
            "1013" -> R.string.srverr_load_notifications
            "1014" -> R.string.srverr_load_notifications_token
            "1015" -> R.string.srverr_load_notifications_lastid
            "1016" -> R.string.srverr_load_notifications_uid
            "1017" -> R.string.srverr_auth_token
            "1018" -> R.string.srverr_load_notifications_username
            "1019" -> R.string.srverr_fcm_submitted_empty
            "1020" -> R.string.srverr_home_view
            else -> R.string.srverr_unknown // Error response "1000" if on purpose I suppose
        }
    }

    /**
     *
     * TIME-SINCE FUNCTIONS
     *
     */
    fun stringToDate(string: String) : Date {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return try {
            // TODO: 2/5/2021 Remove this assertion
            format.parse(string)!!
        } catch (e: ParseException) {
            e.printStackTrace()
            currentDate()
        }
    }

    fun currentDate(): Date {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"))
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"))
        return calendar.time
    }

    fun currentDateSendMessage() = currentDate().toFormattedString()


    fun Date.toFormattedString(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(this)
    }

    fun getTimeAgo(dateString: String, ctx: Context): String? {
        val date = stringToDate(dateString)
        val time: Long = date.time
        val curDate: Date = currentDate()
        val now: Long = curDate.time
        if (time > now || time <= 0) {
            return null
        }
        val timeAgo = when (val dim = getTimeDistanceInMinutes(time)) {
            0 -> ctx.resources.getString(R.string.date_util_term_less) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_minute)
            1 -> "1 " + ctx.resources.getString(R.string.date_util_unit_minute)
            in 2..50 -> dim.toString() + " " + ctx.resources.getString(R.string.date_util_unit_minutes)
            in 51..89 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_an) + " " + ctx.resources.getString(R.string.date_util_unit_hour)
            in 90..1439 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + (dim / 60.toFloat()).roundToInt() + " " + ctx.resources.getString(R.string.date_util_unit_hours)
            in 1440..2519 -> "1 " + ctx.resources.getString(R.string.date_util_unit_day)
            in 2520..43199 -> (dim / 1440.toFloat()).roundToInt().toString() + " " + ctx.resources.getString(R.string.date_util_unit_days)
            in 43200..86399 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_month)
            in 86400..525599 -> (dim / 43200.toFloat()).roundToInt().toString() + " " + ctx.resources.getString(R.string.date_util_unit_months)
            in 525600..655199 -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_year)
            in 655200..914399 -> ctx.resources.getString(R.string.date_util_prefix_over) + " " + ctx.resources.getString(R.string.date_util_term_a) + " " + ctx.resources.getString(R.string.date_util_unit_year)
            in 914400..1051199 -> ctx.resources.getString(R.string.date_util_prefix_almost) + " 2 " + ctx.resources.getString(R.string.date_util_unit_years)
            else -> ctx.resources.getString(R.string.date_util_prefix_about) + " " + (dim / 525600.toFloat()).roundToInt() + " " + ctx.resources.getString(R.string.date_util_unit_years)
        }
        return timeAgo + " " + ctx.resources.getString(R.string.date_util_suffix)
    }

    fun isUserOnline(dateString: String): Boolean {
        val date = stringToDate(dateString)
        val time: Long = date.time
        val curDate: Date = currentDate()
        val now: Long = curDate.time
        if (time > now || time <= 0) {
            return false
        }
        return when (getTimeDistanceInMinutes(time)) {
            in 0..5 -> true
            else -> false
        }
    }

    private fun getTimeDistanceInMinutes(time: Long): Int {
        val timeDistance: Long = currentDate().time - time
        return (abs(timeDistance) / 1000 / 60.toFloat()).roundToInt()
    }

    /**
     * Adds TextWatcher to the EditText
     */
    fun EditText.onTextChanged(listener: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun latLngToLatLngForSomeReason(latLng: com.google.android.gms.maps.model.LatLng) : LatLng {
        return LatLng(latLng.latitude, latLng.longitude)
    }

    fun latLngListToLatLngListForSomeOtherReason(latLngs: List<com.google.maps.model.LatLng>) : Collection<com.google.android.gms.maps.model.LatLng> {
        val list = arrayListOf<com.google.android.gms.maps.model.LatLng>()
        for (latlng in latLngs){
            list.add(com.google.android.gms.maps.model.LatLng(latlng.lat, latlng.lng))
        }
        return list
    }

    fun Fragment.handleApiError(
            failure: Result.Error,
            retry: (() -> Unit)? = null
    ) {
        when (failure.statusCode) {
            /*failure.isNetworkError -> requireView().snackbar(
                    "Please check your internet connection",
                    retry
            )*/
            401 -> {
                (this as BaseFragment<*, *, *>).logout()
            }
            else -> {
                val error = failure.message.toString()
                requireView().snackbar(error)
            }
        }
    }

    fun BaseViewModel.handleResponseError(
            resultCode: String
    ) {
        when (resultCode) {
            "1017" -> {
                showSnackBarInt.value = resultCode.getServerResponseInt()
                userAuthExpired.value = true
            }
            else -> {
                showSnackBarInt.value = resultCode.getServerResponseInt()
            }
        }
    }

}

