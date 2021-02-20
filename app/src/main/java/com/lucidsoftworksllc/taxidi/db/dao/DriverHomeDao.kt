package com.lucidsoftworksllc.taxidi.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeLogEntity
import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeNewsEntity

@Dao
interface DriverHomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(entity: DriverHomeLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(entity: DriverHomeNewsEntity): Long

    @Query("SELECT * FROM home_log_table ORDER BY id DESC")
    fun getHomeLogs(): LiveData<List<DriverHomeLogEntity>>

    @Query("SELECT id FROM home_log_table ORDER BY id DESC LIMIT 1")
    fun getLogLastID(): Long

    @Query("SELECT * FROM home_news_table ORDER BY id DESC")
    fun getHomeNews(): LiveData<List<DriverHomeNewsEntity>>

    @Query("SELECT id FROM home_news_table ORDER BY id DESC LIMIT 1")
    fun getNewsLastID(): Long

}