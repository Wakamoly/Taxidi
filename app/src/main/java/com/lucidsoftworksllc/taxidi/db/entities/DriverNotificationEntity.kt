package com.lucidsoftworksllc.taxidi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucidsoftworksllc.taxidi.others.models.server_responses.ProfileNotification

@Entity(tableName = "driver_notification_table")
data class DriverNotificationEntity(
    @PrimaryKey(autoGenerate = false) var id: Long,
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

fun List<DriverNotificationEntity>.asDomainModel(): List<ProfileNotification> {
    return map {
        ProfileNotification(
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