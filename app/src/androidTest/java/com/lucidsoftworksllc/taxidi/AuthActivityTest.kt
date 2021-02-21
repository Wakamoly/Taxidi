package com.lucidsoftworksllc.taxidi

import android.app.Application
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.lucidsoftworksllc.taxidi.auth.AuthActivity
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.DriverMainActivityRepository
import com.lucidsoftworksllc.taxidi.com.lucidsoftworksllc.taxidi.main_activities.driver_user.fragments.repositories.api.DriverMainAPI
import com.lucidsoftworksllc.taxidi.others.datastore.UserPreferences
import com.lucidsoftworksllc.taxidi.utils.RemoteDataSource
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.DataBindingIdlingResource
import testUtils.EspressoIdlingResource.countingIdlingResource
import testUtils.monitorActivity


@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthActivityTest {

    private lateinit var repository: DriverMainActivityRepository
    private lateinit var appContext: Application
    private val dataBindingIdlingResource = DataBindingIdlingResource()
    private lateinit var userPreferences: UserPreferences

    @Before
    fun init() {
        appContext = ApplicationProvider.getApplicationContext()
        userPreferences = UserPreferences(appContext)
        runBlocking { userPreferences.clear() }
        repository = DriverMainActivityRepository(
                userPreferences,
                RemoteDataSource().buildApi(
                        DriverMainAPI::class.java
                ))
    }

    @Before
    fun registerIdlingResources() {
        IdlingRegistry.getInstance().apply {
            register(countingIdlingResource)
            register(dataBindingIdlingResource)
        }
    }

    @After
    fun unregisterIdlingResources() {
        IdlingRegistry.getInstance().apply {
            unregister(countingIdlingResource)
            unregister(dataBindingIdlingResource)
        }
    }

    @Test
    fun launch_signIn() {
        val scenario = ActivityScenario.launch(AuthActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        // 2/20/2021 typeText not working when user signed into a google account on the device, as it pops up with auto-enter Google credentials
        // to combat this, remove any Google accounts on the device and disable animations
        Espresso.onView(withId(R.id.login_as_driver)).perform(click())
        Thread.sleep(250)
        Espresso.onView(withId(R.id.sign_in_email_address))
                .perform(ViewActions.typeText("truckdrivingman@yeehaw.com"))
        Thread.sleep(250)
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.sign_in_password))
                .perform(ViewActions.typeText("ffff"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.signin_frag_login_button)).perform(click())

    }

    @Test
    fun launch_register() {
        val scenario = ActivityScenario.launch(AuthActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        Espresso.onView(withId(R.id.login_as_driver)).perform(click())
        Thread.sleep(250)
        Espresso.onView(withId(R.id.sign_in_email_address))
                .perform(ViewActions.typeText("truckdrivingman222@yeehaw.com"))
        Thread.sleep(250)
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.sign_in_password))
                .perform(ViewActions.typeText("ffff"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.signin_frag_register_button)).perform(click())

        Espresso.onView(withId(R.id.sign_in_username))
                .perform(ViewActions.typeText("TruckDrivingMan222"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.register_password2))
                .perform(ViewActions.typeText("ffff"))
        Espresso.closeSoftKeyboard()
        Espresso.onView(withId(R.id.register_frag_register_button)).perform(click())
        Thread.sleep(250)

        Espresso.onView(withId(R.id.auth_register2_autocomplete)).perform(click())
        Thread.sleep(250)
        Espresso.onView(withId(R.id.auth_register2_autocomplete))
                .perform(ViewActions.replaceText(appContext.getString(R.string.register_authority_type_3)))

        Espresso.onView(withId(R.id.auth_register2_autocomplete_type)).perform(click())
        Thread.sleep(250)
        Espresso.onView(withId(R.id.auth_register2_autocomplete_type))
                .perform(ViewActions.replaceText(appContext.getString(R.string.register_driver_type_4)))



    }

}