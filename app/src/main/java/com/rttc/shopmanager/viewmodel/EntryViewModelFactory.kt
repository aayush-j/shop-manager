package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rttc.shopmanager.database.EntryRepository

class EntryViewModelFactory(private val entryRepository: EntryRepository, private val entryId: Long)
    :ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EntryViewModel(entryRepository, entryId) as T
    }
}