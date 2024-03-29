package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rttc.shopmanager.database.EntryRepository

class ModifyViewModelFactory(private val entryRepository: EntryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ModifyViewModel(entryRepository) as T
    }
}