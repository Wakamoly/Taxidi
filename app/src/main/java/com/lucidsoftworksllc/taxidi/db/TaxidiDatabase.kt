package com.lucidsoftworksllc.taxidi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


/*@Database(
    entities = [
       // ShoeEntity::class,
        //UserEntity::class
    ],
    version = 1)*/
@TypeConverters(DbConverters::class)
abstract class TaxidiDatabase : RoomDatabase() {

    //abstract fun getUserDao() : UserDao
    //abstract fun getShoeDao() : ShoeDao

    companion object{
        val DATABASE_NAME: String = "taxididatabase"
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