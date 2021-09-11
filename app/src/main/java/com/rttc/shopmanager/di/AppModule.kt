package com.rttc.shopmanager.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rttc.shopmanager.database.EntryDao
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.database.ShopDatabase
import com.rttc.shopmanager.utilities.DB_NAME
import com.rttc.shopmanager.utilities.PREFS_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(application: Application) {
    private val appContext: Application = application

    @Provides
    @Singleton
    fun provideShopDatabase(): ShopDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            ShopDatabase::class.java,
            DB_NAME
        )
            .addMigrations(MIGRATION_5_6)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    @Provides
    @Singleton
    fun provideEntryDao(shopDatabase: ShopDatabase) = shopDatabase.entryDao()

    @Provides
    @Singleton
    fun provideEntryRepository(entryDao: EntryDao) = EntryRepository(entryDao)

    @Provides
    @Singleton
    fun provideSharedPreferences() = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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