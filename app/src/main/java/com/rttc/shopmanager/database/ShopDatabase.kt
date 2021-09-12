package com.rttc.shopmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Entry::class, Category::class], version = 6, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class ShopDatabase: RoomDatabase() {
    abstract fun entryDao(): EntryDao
}