package com.rttc.shopmanager.database

import android.util.Log
import com.rttc.shopmanager.utilities.LOG_PREFIX
import java.time.LocalDate

class EntryRepository private constructor(private val entryDao: EntryDao) {
    fun getAllEntries() = entryDao.getAllEntries()

    suspend fun insert(entry: Entry) {
        val recId = entryDao.insert(entry)
        Log.d(LOG_PREFIX, "Insert, recId = $recId\nDetails: \n$entry")
    }

    suspend fun update(entry: Entry) = entryDao.update(entry)

    suspend fun delete(entry: Entry) = entryDao.delete(entry)

    suspend fun deleteAllByCategory(category: String) = entryDao.deleteAllByCategory(category)

    suspend fun insertAll(entries: List<Entry>) = entryDao.insertAll(entries)

    suspend fun insertCategory(category: Category) = entryDao.insertCategory(category)

    suspend fun deleteCategory(category: Category) = entryDao.deleteCategory(category)

    fun getEntryById(id: Long) = entryDao.getEntryById(id)

    fun getEntriesByEnquiryType(enquiryType: String) = entryDao.getEntriesByEnquiryType(enquiryType)

    fun getEntriesByStatus(status: String) = entryDao.getEntriesByStatus(status)

    fun getEntriesByFilter(enquiryType: String, status: String) =
        entryDao.getEntriesByFilter(enquiryType, status)

    fun getTableCount() = entryDao.getTableCount()

    fun getAllCategories() = entryDao.getAllCategories()

    companion object {
        @Volatile private var INSTANCE: EntryRepository? = null
        fun getInstance(entryDao: EntryDao): EntryRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: EntryRepository(entryDao).also { INSTANCE = it }
            }
        }
    }
}