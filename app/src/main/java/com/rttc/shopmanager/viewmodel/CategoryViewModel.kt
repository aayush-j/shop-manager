package com.rttc.shopmanager.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rttc.shopmanager.database.Category
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val entryRepository: EntryRepository
) : ViewModel() {

    val categories: LiveData<List<Category>> = entryRepository.getAllCategories()

    fun insert(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.insertCategory(category)
        }
    }

    fun delete(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            entryRepository.deleteCategory(category)
        }
    }

}