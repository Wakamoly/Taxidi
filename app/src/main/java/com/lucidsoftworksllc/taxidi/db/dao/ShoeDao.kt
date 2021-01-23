package com.lucidsoftworksllc.taxidi.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lucidsoftworksllc.taxidi.db.entities.ShoeEntity

@Dao
interface ShoeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoeEntity: ShoeEntity) : Long

    @Query("SELECT * FROM shoe_table ORDER BY id DESC")
    suspend fun get(): List<ShoeEntity>

    @Query("UPDATE shoe_table SET name = :name, size = :size, company = :company, description = :description, images = :images WHERE id = :shoeID")
    suspend fun update(name: String,
                       size: Double,
                       company: String,
                       description: String,
                       images: String,
                       shoeID: Int) : Int

}