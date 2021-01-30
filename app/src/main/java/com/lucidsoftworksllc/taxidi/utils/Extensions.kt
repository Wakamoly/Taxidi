package com.lucidsoftworksllc.taxidi.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lucidsoftworksllc.taxidi.R
import com.lucidsoftworksllc.taxidi.base.BaseRecyclerViewAdapter
import com.lucidsoftworksllc.taxidi.base.BaseViewModel
import com.lucidsoftworksllc.taxidi.base.NavigationCommand
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern


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

fun View.snackbar(message: String, actionText: String, action: (() -> Unit)? = null){
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
}

val Fragment.fcmToken: String
    get() {
        var authToken = ""
        this.lifecycleScope.launch {
            authToken = UserPreferences(requireContext()).fCMToken()
        }
        Log.d("Extensions", "fcmToken Fragment: $authToken")
        return authToken
    }

val Activity.fcmToken: String
    get() {
        var authToken = ""
        CoroutineScope(Dispatchers.Main).launch {
            authToken = UserPreferences(this@fcmToken).fCMToken()
        }
        Log.d("Extensions", "fcmToken: $authToken")
        return authToken
    }

val Activity.isUserLoggedIn: Boolean
    get() {
        var isUserLoggedIn = false
        CoroutineScope(Dispatchers.Main).launch {
            isUserLoggedIn = UserPreferences(this@isUserLoggedIn).isUserLoggedIn()
        }
        Log.d("Extensions", "isUserLoggedIn: $isUserLoggedIn")
        return isUserLoggedIn
    }

fun String.getServerResponseInt(): Int {
    // Server responses coded 0xxx for Success values and 1xxx for failures.
    // This is done instead of a response string from the server. A code is given, then decoded here for locality
    return when(this) {
        // Success
        "0001" -> R.string.srvsuc_register
        "0002" -> R.string.srvsuc_login

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
        else -> R.string.srverr_unknown
    }
}

