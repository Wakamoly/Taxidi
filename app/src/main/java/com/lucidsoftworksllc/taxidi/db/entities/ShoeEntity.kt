package com.lucidsoftworksllc.taxidi.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "shoe_table")
data class ShoeEntity(
    var name: String,
    var size: Double,
    var company: String,
    var description: String,
    var images: List<String> = mutableListOf()
) : Parcelable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
