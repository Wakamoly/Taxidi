package com.lucidsoftworksllc.taxidi.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    var username: String,
    var email: String,
    var token: String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
