package com.lucidsoftworksllc.taxidi.others.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegisterModel(
        val authorityType : String,
        val type : String,
        val companyName : String,
        val streetAddress : String,
        val city : String,
        val state : String,
        val zipCode : String,
        val country : String,
        val companyPhone : String,
        val firstName : String,
        val lastName : String,
        val personalPhone : String
) : Parcelable