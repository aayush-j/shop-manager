package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyViewModel(
    private val entryRepository: EntryRepository,
    private val entryId: Long
) : ViewModel() {

    fun getEntry() = entryRepository.getEntryById(entryId)

    fun insertEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.insert(entry)
        }
    }

    fun updateEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.update(entry)
        }
    }
}