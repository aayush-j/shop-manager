package com.rttc.shopmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rttc.shopmanager.utilities.DB_NAME


@Database(entities = [Entry::class, Category::class], version = 6, exportSchema = false)
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
                ).addMigrations(MIGRATION_5_6).build()
            }
        }

        private val MIGRATION_5_6 = object : Migration(5,6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE register_temp (id INTEGER NOT NULL, name TEXT NOT NULL, primary_contact TEXT NOT NULL, secondary_contact TEXT NOT NULL, whatsApp_contact TEXT NOT NULL, email TEXT NOT NULL, address TEXT NOT NULL, enquiry_type TEXT NOT NULL, enquiry_details TEXT NOT NULL, status TEXT NOT NULL, date_opened INTEGER, date_closed INTEGER, PRIMARY KEY(id))"
                )

                database.execSQL(
                    "INSERT INTO register_temp (id, name, primary_contact, secondary_contact, whatsApp_contact, email, address, enquiry_type, enquiry_details, status) SELECT id, name, primary_contact, secondary_contact, whatsApp_contact, email, address, enquiry_type, enquiry_details, status FROM register"
                )

                database.execSQL("DROP TABLE register")

                database.execSQL("ALTER TABLE register_temp RENAME TO register")
            }
        }
    }
}