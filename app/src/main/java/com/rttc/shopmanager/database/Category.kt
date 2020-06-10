package com.rttc.shopmanager.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "category")
class Category(
    @PrimaryKey
    var title: String
)