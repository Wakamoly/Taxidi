package com.lucidsoftworksllc.taxidi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lucidsoftworksllc.taxidi.others.models.server_responses.DriverHomeViewResponseModel
import com.lucidsoftworksllc.taxidi.others.models.server_responses.ProfileNotification

@Entity(tableName = "home_log_table")
data class DriverHomeLogEntity(
        @PrimaryKey(autoGenerate = false) var id: Int,
        var active: Int,
        var text: String
)

@JvmName("asDomainModelDriverHomeLogEntity")
fun List<DriverHomeLogEntity>.asDomainModel(): List<DriverHomeViewResponseModel.LogResult> {
    return map {
        DriverHomeViewResponseModel.LogResult(
                id = it.id,
                active = it.active,
                text = it.text
        )
    }
}

@Entity(tableName = "home_news_table")
data class DriverHomeNewsEntity(
        @PrimaryKey(autoGenerate = false) var id: Int,
        var date: String,
        var desc: String,
        var display_name: String,
        var news_user_id: Int,
        var news_username: String,
        var profile_pic: String,
        var title: String
)

fun List<DriverHomeNewsEntity>.asDomainModel(): List<DriverHomeViewResponseModel.NewsResult> {
    return map {
        DriverHomeViewResponseModel.NewsResult(
                id = it.id,
                date = it.date,
                desc = it.desc,
                display_name = it.display_name,
                news_user_id = it.news_user_id,
                news_username = it.news_username,
                profile_pic = it.profile_pic,
                title = it.title
        )
    }
}
