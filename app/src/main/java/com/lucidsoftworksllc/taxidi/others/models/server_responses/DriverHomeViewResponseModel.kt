package com.lucidsoftworksllc.taxidi.others.models.server_responses

import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeLogEntity
import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeNewsEntity
import com.lucidsoftworksllc.taxidi.db.entities.DriverNotificationEntity

/*{
    "error": false,
    "code": "0002",
    "top_result": {
        "verified": 0,
        "numshipped": 0
    },
    "log_result": [
        {
            "id": 1,
            "text": "Yeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeehaw",
            "active": 1
        }
    ],
    "news_result": [
        {
            "id": 1,
            "news_username": "wakamoly",
            "display_name": "Wakamoly",
            "title": "Test Title",
            "desc": "Test description and what-not.",
            "date": "2021-02-19 09:21:14",
            "news_user_id": 4,
            "profile_pic": "assets/images/profile_pics/defaults/taxidi/Taxidi_Logo.png"
        }
    ]
}*/

data class DriverHomeViewResponseModel(
    val code: String,
    val error: Boolean,
    val log_result: List<LogResult>?,
    val news_result: List<NewsResult>?,
    val top_result: StatsTopResult?
) {
    data class LogResult(
        val active: Int,
        val id: Int,
        val text: String
    ) {
        fun asDatabaseModel(): DriverHomeLogEntity {
            return DriverHomeLogEntity(
                    id = this.id,
                    active = this.active,
                    text = this.text
            )
        }
    }

    data class NewsResult(
        val date: String,
        val desc: String,
        val display_name: String,
        val id: Int,
        val news_user_id: Int,
        val news_username: String,
        val profile_pic: String,
        val title: String
    ) {
        fun asDatabaseModel(): DriverHomeNewsEntity {
            return DriverHomeNewsEntity(
                    id = this.id,
                    date = this.date,
                    desc = this.desc,
                    display_name = this.display_name,
                    news_username = this.news_username,
                    news_user_id = this.news_user_id,
                    profile_pic = this.profile_pic,
                    title = this.title
            )
        }
    }

    data class StatsTopResult(
        val numshipped: Int,
        val verified: Int
    )
}



/*
@JvmName("asDatabaseModelLogResultDriverHomeViewResponseModel")
fun List<DriverHomeViewResponseModel.LogResult>.asDatabaseModel(): List<DriverHomeLogEntity> {
    return map {
        DriverHomeLogEntity(
                id = it.id,
                active = it.active,
                text = it.text
        )
    }
}

fun List<DriverHomeViewResponseModel.NewsResult>.asDatabaseModel(): List<DriverHomeNewsEntity> {
    return map {
        DriverHomeNewsEntity(
                id = it.id,
                date = it.date,
                desc = it.desc,
                display_name = it.display_name,
                news_username = it.news_username,
                news_user_id = it.news_user_id,
                profile_pic = it.profile_pic,
                title = it.title
        )
    }
}*/
