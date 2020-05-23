package com.rttc.shopmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EntryDao {
    @Query("SELECT id, name, enquiry_type, status, date_opened  FROM register ORDER BY id DESC")
    fun getAllEntries(): LiveData<List<EntryLite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<Entry>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    @Query("SELECT * FROM register WHERE id = :id")
    fun getEntryById(id: Long): LiveData<Entry>

    @Query("SELECT id, name, enquiry_type, status, date_opened FROM register WHERE enquiry_type = :enquiryType ORDER BY id DESC")
    fun getEntriesByEnquiryType(enquiryType: String): LiveData<List<EntryLite>>

    @Query("SELECT id, name, enquiry_type, status, date_opened FROM register WHERE status = :status ORDER BY id DESC")
    fun getEntriesByStatus(status: String): LiveData<List<EntryLite>>

    @Query("SELECT id, name, enquiry_type, status, date_opened FROM register WHERE enquiry_type = :enquiryType AND status = :status ORDER BY id DESC")
    fun getEntriesByFilter(enquiryType: String, status: String): LiveData<List<EntryLite>>

    @Query("SELECT COUNT(id) from register")
    fun getTableCount(): LiveData<Int>
}