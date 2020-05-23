package com.rttc.shopmanager.utilities

import android.content.Context
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.database.ShopDatabase
import com.rttc.shopmanager.viewmodel.EntryViewModelFactory
import com.rttc.shopmanager.viewmodel.HomeViewModelFactory
import com.rttc.shopmanager.viewmodel.ModifyViewModelFactory

object Instances {
    private fun getEntryRepository(context: Context) =
        EntryRepository.getInstance(ShopDatabase.getInstance(context).entryDao())

    fun provideHomeViewModelFactory(context: Context) =
        HomeViewModelFactory(getEntryRepository(context))

    fun provideEntryViewModelFactory(context: Context, entryId: Long) =
        EntryViewModelFactory(getEntryRepository(context), entryId)

    fun provideModifyViewModelFactory(context: Context, entryId: Long) =
        ModifyViewModelFactory(getEntryRepository(context), entryId)
}