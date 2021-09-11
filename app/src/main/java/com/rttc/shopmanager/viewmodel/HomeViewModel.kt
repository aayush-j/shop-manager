package com.rttc.shopmanager.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.ui.FilterLiveData
import com.rttc.shopmanager.utilities.TYPE_ALL

private val TAG = HomeViewModel::class.simpleName

class HomeViewModel(private val entryRepository: EntryRepository) : ViewModel() {
    val enquiryType: MutableLiveData<String> = MutableLiveData(TYPE_ALL)
    val statusType: MutableLiveData<String> = MutableLiveData(TYPE_ALL)
    private val filter = FilterLiveData(enquiryType, statusType)

    fun applyFilter(enquiry: String, status: String) {
        enquiryType.value = enquiry
        statusType.value = status
    }

    val entryList = Transformations.switchMap(filter) {
        Log.d(TAG, "filter value = ${it.first}, ${it.second}")

        if (it.first == TYPE_ALL && it.second == TYPE_ALL)
            entryRepository.getAllEntries()
        else if (it.first == TYPE_ALL)
            entryRepository.getEntriesByStatus(it.second)
        else if (it.second == TYPE_ALL)
            entryRepository.getEntriesByEnquiryType(it.first)
        else
            entryRepository.getEntriesByFilter(it.first, it.second)
    }

    fun searchDb(query: String) = entryRepository.searchByName(query)
}