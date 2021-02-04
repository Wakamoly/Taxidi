package com.lucidsoftworksllc.taxidi.others.models.server_responses

/*{
    "error": false,
    "code": "0003",
    "result": {
        "display_name": "wakamoly",
        "description": "",
        "type": "driver",
        "profile_pic": "assets/images/profile_pics/defaults/sabotblack.gif",
        "back_pic": "assets/images/backgrounds/taxidi/driver_type_back_default.png",
        "user_closed": 0,
        "user_banned": 0,
        "verified": 0,
        "last_online": "2021-02-03 19:39:18",
        "num_shipped": 0,
        "status": 0,
        "average": 5
    }
}*/

data class DriverProfileResponseModel(
    val error: Boolean,
    val code: String,
    val result: DriverProfileCredentialModel?
)

data class DriverProfileCredentialModel(
    val display_name: String,
    val description: String,
    val type: String,
    val profile_pic: String,
    val back_pic: String,
    val user_closed: Int,
    val user_banned: Int,
    val verified: Int,
    val last_online: String,
    val num_shipped: Int,
    val status: Int,
    // May cause a problem
    val average: Int
)
