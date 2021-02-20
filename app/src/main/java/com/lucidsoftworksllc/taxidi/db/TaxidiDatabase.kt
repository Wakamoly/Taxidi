package com.lucidsoftworksllc.taxidi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lucidsoftworksllc.taxidi.db.dao.DriverHomeDao
import com.lucidsoftworksllc.taxidi.db.dao.DriverNotificationDao
import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeLogEntity
import com.lucidsoftworksllc.taxidi.db.entities.DriverHomeNewsEntity
import com.lucidsoftworksllc.taxidi.db.entities.DriverNotificationEntity


@Database(
        entities = [
            DriverNotificationEntity::class,
            DriverHomeNewsEntity::class,
            DriverHomeLogEntity::class
        ],
        version = 2)
@TypeConverters(DbConverters::class)
abstract class TaxidiDatabase : RoomDatabase() {

    abstract fun getDriverNotiDao() : DriverNotificationDao
    abstract fun getDriverHomeDao() : DriverHomeDao

    companion object{
        const val DATABASE_NAME: String = "taxididatabase"
        @Volatile private var instance : TaxidiDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        /**
         *
         * fallbackToDestructive since we don't care if we lose data on changed schema
         * otherwise, we could create our own migrations via explaining changes to the DB about changed entities
         *
         * */
        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            TaxidiDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    }

}