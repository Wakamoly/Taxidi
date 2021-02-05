package com.lucidsoftworksllc.taxidi.others.models.server_responses

import com.lucidsoftworksllc.taxidi.db.entities.DriverNotificationEntity

/*{
    "error": false,
    "code": "0004",
    "result": [
    {
        "id": 2,
        "user_from": "truckdrivingman",
        "title": "TruckDrivingMan left you a review!",
        "message": "4 star rating:\r\n\"Gud dude\"\r\n\"Much gud dude\"",
        "type": "reviewed",
        "link": "review=1",
        "datetime": "2021-02-04 23:07:40",
        "opened": 1,
        "viewed": 1,
        "user_id": 6,
        "profile_pic": "assets/images/profile_pics/defaults/taxidi/Taxidi_Logo.png",
        "display_name": "truckdrivingman",
        "last_online": "2021-02-02 20:26:13",
        "deleted": 0
    }
    ]
}*/

data class DriverNotificationsResponseModel(
    val code: String,
    val error: Boolean,
    val result: List<ProfileNotification>?
)

data class ProfileNotification(
    val id: Long,
    val datetime: String,
    val deleted: Int,
    val display_name: String,
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

fun List<ProfileNotification>.asDatabaseModel(): List<DriverNotificationEntity> {
    return map {
        DriverNotificationEntity(
            id = it.id,
            datetime = it.datetime,
            deleted = it.deleted,
            display_name = it.display_name,
            last_online = it.last_online,
            link = it.link,
            title = it.title,
            message = it.message,
            opened = it.opened,
            profile_pic = it.profile_pic,
            type = it.type,
            user_from = it.user_from,
            user_id = it.user_id,
            viewed = it.viewed
        )
    }
}