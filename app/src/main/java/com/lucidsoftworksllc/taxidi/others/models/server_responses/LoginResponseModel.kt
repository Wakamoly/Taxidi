package com.lucidsoftworksllc.taxidi.others.models.server_responses


/*
{
    "error": false,
    "code": "0002",
    "result": {
        "user_id": 1,
        "username": "wakamoly1",
        "type": "driver",
        "auth_token": "3KnjM_0b9AJtr4G_ORi-NvYHjuLaLgP4rUPfdq8e1z6wXHGyROyBFgDGIn__3dlN6t70_9tgOp1Bz9esxHWACQ=="
    }
}
*/

data class LoginResponseModel(
    val error: Boolean,
    val code: String,
    val result: LoginCredentialModel?
)

data class LoginCredentialModel (
    val user_id: Int,
    val username: String,
    val type: String,
    // One instance per device, do not expire, only updated when the user signs in again
    val auth_token: String
)
