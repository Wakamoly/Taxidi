package com.lucidsoftworksllc.taxidi.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucidsoftworksllc.taxidi.db.entities.DriverNotificationEntity

@Dao
interface DriverNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(driverNotificationEntities: List<DriverNotificationEntity>)

    @Query("SELECT * FROM driver_notification_table ORDER BY id DESC")
    fun getNotifications(): LiveData<List<DriverNotificationEntity>>

    @Query("SELECT id FROM driver_notification_table ORDER BY id DESC LIMIT 1")
    fun getLastID(): Long

    @Query("UPDATE driver_notification_table SET opened = 1, viewed = 1 WHERE opened = 0 OR viewed = 0")
    suspend fun setAllOpened()

    @Query("UPDATE driver_notification_table SET opened = 1, viewed = 1 WHERE id = :id")
    suspend fun setOpened(id: Long)

}