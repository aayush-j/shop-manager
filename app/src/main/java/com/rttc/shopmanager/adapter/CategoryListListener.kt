package com.rttc.shopmanager.adapter

import com.rttc.shopmanager.database.Category

interface CategoryListListener {
    fun onButtonClick(category: Category)
}