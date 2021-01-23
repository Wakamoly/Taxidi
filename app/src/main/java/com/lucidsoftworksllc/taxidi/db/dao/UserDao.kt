package com.lucidsoftworksllc.taxidi.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucidsoftworksllc.taxidi.db.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userEntity: UserEntity): Long

    @Query("SELECT * FROM user_table WHERE username = :username LIMIT 1")
    suspend fun getUser(username: String) : UserEntity

    @Query("SELECT EXISTS (SELECT * FROM user_table WHERE username = :username LIMIT 1)")
    suspend fun isUserRetrievable(username: String) : Boolean

}