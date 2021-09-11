package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyViewModel(private val entryRepository: EntryRepository) : ViewModel() {

    val entryId = MutableLiveData<Long>(-1)

    val entry = Transformations.switchMap(entryId) {
        entryRepository.getEntryById(it)
    }

    fun insertEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.insert(entry)
        }
    }

    fun updateEntry(entry: Entry) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.updateEntry(entry)
        }
    }

    val categoryList = entryRepository.getCategoryList()
}