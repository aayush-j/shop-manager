package com.rttc.shopmanager.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class FilterLiveData(
    enquiryType: LiveData<String>,
    statusType: LiveData<String>
): MediatorLiveData<Pair<String, String>>() {
    init {
        addSource(enquiryType) {
            value = Pair(it, statusType.value!!)
        }
        addSource(statusType) {
            value = Pair(enquiryType.value!!, it)
        }
    }
}