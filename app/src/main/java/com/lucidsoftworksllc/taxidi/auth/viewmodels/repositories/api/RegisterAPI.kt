package com.lucidsoftworksllc.taxidi.auth.viewmodels.repositories.api

import retrofit2.http.*

interface RegisterAPI {

    @GET("dashboardads_api.php")
    suspend fun getAds(
            @Query("username") username: String?
    ): List<DashboardAdModelItem>

    @FormUrlEncoded
    @POST("dashboardfeed_api.php")
    suspend fun getDashFeed(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: Int?,
            @Field("method") method: String?
    ): List<ProfilenewsRecycler>

}