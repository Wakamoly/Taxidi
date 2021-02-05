package com.lucidsoftworksllc.taxidi.others.models.server_responses

data class DriverNotificationsResponseModel(
    val code: String,
    val error: Boolean,
    val result: List<ProfileNotification>
) {
    data class ProfileNotification(
        val datetime: String,
        val deleted: Int,
        val display_name: String,
        val id: Int,
        val last_online: String,
        val link: String,
        val title: String,
        val message: String,
        val opened: Int,
        val profile_pic: String,
        val type: String,
        val user_from: String,
        val user_id: Int,
        val viewed: Int
    )
}