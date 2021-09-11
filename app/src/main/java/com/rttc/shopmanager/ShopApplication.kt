package com.rttc.shopmanager

import android.app.Application
import com.rttc.shopmanager.di.AppModule
import com.rttc.shopmanager.di.DaggerShopComponent
import com.rttc.shopmanager.di.ShopComponent

class ShopApplication : Application() {
    lateinit var shopComponent: ShopComponent

    override fun onCreate() {
        super.onCreate()
        shopComponent = DaggerShopComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}