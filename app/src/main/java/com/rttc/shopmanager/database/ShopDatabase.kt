package com.rttc.shopmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rttc.shopmanager.utilities.DB_NAME

@Database(entities = [Entry::class, Category::class], version = 5, exportSchema = false)
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
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
            }
        }
    }
}