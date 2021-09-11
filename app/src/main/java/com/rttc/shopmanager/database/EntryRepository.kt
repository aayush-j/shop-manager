package com.rttc.shopmanager.database

import javax.inject.Inject

class EntryRepository @Inject constructor(private val entryDao: EntryDao) {
    fun getAllEntries() = entryDao.getAllEntries()

    suspend fun insert(entry: Entry) = entryDao.insert(entry)

    suspend fun updateEntry(entry: Entry) = entryDao.update(entry)

    suspend fun deleteEntry(entry: Entry) = entryDao.delete(entry)

    fun getEntryById(id: Long) = entryDao.getEntryById(id)

    fun getEntriesByEnquiryType(enquiryType: String) = entryDao.getEntriesByEnquiryType(enquiryType)

    fun getEntriesByStatus(status: String) = entryDao.getEntriesByStatus(status)

    fun getEntriesByFilter(enquiryType: String, status: String) =
        entryDao.getEntriesByFilter(enquiryType, status)

    fun searchByName(name: String) = entryDao.searchByName(name)

    //fun getTableCount() = entryDao.getTableCount()

    fun getCategoryList() = entryDao.getAllCategories()

    suspend fun deleteCategoryList(category: String) = entryDao.deleteAllByCategory(category)

    suspend fun insertCategory(category: Category) = entryDao.insertCategory(category)

    suspend fun deleteCategory(category: Category) = entryDao.deleteCategory(category)
}