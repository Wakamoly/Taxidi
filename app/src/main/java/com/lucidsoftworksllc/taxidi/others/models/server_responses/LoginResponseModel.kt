package com.lucidsoftworksllc.taxidi.others.models.server_responses


/*{
    "error": false,
    "code": "0002",
    "result": {
    "user_id": 1,
    "username": "wakamoly1",
    "type": "driver"
}
}*/

data class LoginResponseModel(
        val error: Boolean,
        val code: String,
        val result: LoginCredentialModel?
)

data class LoginCredentialModel (
    val user_id: Int,
    val username: String,
    val type: String
)
