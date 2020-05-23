package com.rttc.shopmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Entry::class], version = 2, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ShopDatabase: RoomDatabase() {
    abstract fun entryDao(): EntryDao

    companion object {
        @Volatile private var INSTANCE: ShopDatabase? = null
        fun getInstance(context: Context): ShopDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ShopDatabase::class.java,
                    "shop_database"
                ).fallbackToDestructiveMigration().build()
            }
        }
    }
}