package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryViewModel(
    private val entryRepository: EntryRepository,
    entryId: Long
) : ViewModel() {

    val entry: LiveData<Entry> = entryRepository.getEntryById(entryId)

    fun updateEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.update(entry)
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.delete(entry)
        }
    }

}