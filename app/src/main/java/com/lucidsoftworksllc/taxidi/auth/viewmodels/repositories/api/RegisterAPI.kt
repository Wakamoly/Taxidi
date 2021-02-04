package com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api

import com.lucidsoftworksllc.taxidi.others.models.server_responses.LoginResponseModel
import com.lucidsoftworksllc.taxidi.others.models.server_responses.RegisterResponseModel
import retrofit2.http.*

interface RegisterAPI {

    @FormUrlEncoded
    @POST("users_main.php/register")
    suspend fun register(
            @Field("signInAs") signInAs: String?,
            @Field("username") username: String?,
            @Field("emailAddress") emailAddress: String?,
            @Field("password") password: String?,
            @Field("authorityType") authorityType: String?,
            @Field("type") type: String?,
            @Field("companyName") companyName: String?,
            @Field("streetAddress") streetAddress: String?,
            @Field("city") city: String?,
            @Field("state") state: String?,
            @Field("zipCode") zipCode: String?,
            @Field("country") country: String?,
            @Field("companyPhone") companyPhone: String?,
            @Field("firstName") firstName: String?,
            @Field("lastName") lastName: String?,
            @Field("personalPhone") personalPhone: String?
    ) : RegisterResponseModel

    @GET("users_main.php/login")
    suspend fun login(
            @Query("email") email: String?,
            @Query("password") password: String?
    ): LoginResponseModel

}