package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.*
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.ui.ModifyFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EntryViewModel(private val entryRepository: EntryRepository) : ViewModel() {

    val entId = MutableLiveData<Long>(-1)

    private val receivedEntry: LiveData<Entry> = Transformations.switchMap(entId) {
        entryRepository.getEntryById(it)
    }

    val entry: LiveData<Entry> = Transformations.switchMap(receivedEntry) { newEntry ->
        newEntry?.let {
            val calendar = Calendar.getInstance()
            if (it.dateOpened == null) {
                it.dateOpened = calendar.time
                updateEntry(it)
            }
            if (it.status == ModifyFragment.STATUS_CLOSED && it.dateClosed == null) {
                it.status = ModifyFragment.STATUS_OPEN
                updateEntry(it)
            }
        }
        MutableLiveData(newEntry)
    }

    fun updateEntry(entry: Entry) =
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.updateEntry(entry)
        }

    fun deleteEntry(entry: Entry) =
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.deleteEntry(entry)
        }

    fun closeEntry(entry: Entry) =
        entry.apply {
            status = ModifyFragment.STATUS_CLOSED
            Calendar.getInstance().let { calendar ->
                calendar.timeZone = TimeZone.getTimeZone("IST")
                dateClosed = calendar.time
            }
            updateEntry(this)
        }

    fun openEntry(entry: Entry) =
        entry.apply {
            status = ModifyFragment.STATUS_OPEN
            updateEntry(this)
        }
}