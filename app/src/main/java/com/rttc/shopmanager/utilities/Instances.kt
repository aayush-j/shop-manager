package com.rttc.shopmanager.utilities

import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.viewmodel.CategoryViewModelFactory
import com.rttc.shopmanager.viewmodel.EntryViewModelFactory
import com.rttc.shopmanager.viewmodel.HomeViewModelFactory
import com.rttc.shopmanager.viewmodel.ModifyViewModelFactory

object Instances {
    fun provideHomeViewModelFactory(repository: EntryRepository) =
        HomeViewModelFactory(repository)

    fun provideEntryViewModelFactory(repository: EntryRepository) =
        EntryViewModelFactory(repository)

    fun provideModifyViewModelFactory(repository: EntryRepository) =
        ModifyViewModelFactory(repository)

    fun provideCategoryViewModelFactory(repository: EntryRepository) =
        CategoryViewModelFactory(repository)
}