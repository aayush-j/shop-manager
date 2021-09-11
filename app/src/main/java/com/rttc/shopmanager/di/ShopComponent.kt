package com.rttc.shopmanager.di

import com.rttc.shopmanager.SplashActivity
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.onboarding.CategoryFragment
import com.rttc.shopmanager.onboarding.WelcomeFragment
import com.rttc.shopmanager.ui.*
import com.rttc.shopmanager.utilities.DatabaseHelperActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface ShopComponent {
    fun inject(entryRepository: EntryRepository)
    fun inject(entryFragment: EntryFragment)
    fun inject(filterBottomSheet: FilterBottomSheet)
    fun inject(homeFragment: HomeFragment)
    fun inject(modifyFragment: ModifyFragment)
    fun inject(categoryFragment: CategoryFragment)
    fun inject(searchFragment: SearchFragment)
    fun inject(databaseHelperActivity: DatabaseHelperActivity)
    fun inject(welcomeFragment: WelcomeFragment)
    fun inject(splashActivity: SplashActivity)
}