package com.rttc.shopmanager.viewmodel

import androidx.arch.core.util.Function
import androidx.lifecycle.*
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryLite
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.ui.FilterLiveData
import com.rttc.shopmanager.utilities.TYPE_ALL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val entryRepository: EntryRepository)
    :ViewModel(){

    val enquiryType: MutableLiveData<String> = MutableLiveData(TYPE_ALL)
    val statusType: MutableLiveData<String> = MutableLiveData(TYPE_ALL)
    private val filter =
        FilterLiveData(enquiryType, statusType)
    val entryList: LiveData<List<EntryLite>>

    fun insertAllEnquiries(entries: List<Entry>) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.insertAll(entries)
        }
    }

    init {
        entryList = Transformations.switchMap(filter, Function {
            if (it.first == TYPE_ALL && it.second == TYPE_ALL)
                entryRepository.getAllEntries()
            else if (it.first == TYPE_ALL)
                entryRepository.getEntriesByStatus(it.second)
            else if (it.second == TYPE_ALL)
                entryRepository.getEntriesByEnquiryType(it.first)
            else
                entryRepository.getEntriesByFilter(it.first, it.second)
        })
    }

}