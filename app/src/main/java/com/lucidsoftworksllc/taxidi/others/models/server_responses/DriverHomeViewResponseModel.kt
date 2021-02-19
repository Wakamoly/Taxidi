package com.lucidsoftworksllc.taxidi.others.models.server_responses

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
    )

    data class NewsResult(
        val date: String,
        val desc: String,
        val display_name: String,
        val id: Int,
        val news_user_id: Int,
        val news_username: String,
        val profile_pic: String,
        val title: String
    )

    data class StatsTopResult(
        val numshipped: Int,
        val verified: Int
    )
}