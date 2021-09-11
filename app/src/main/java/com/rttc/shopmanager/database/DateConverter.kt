package com.rttc.shopmanager.database

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun toDate(value: Long?): Date? {
        if (value == null)
            return null
        return Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        if (value == null)
            return null
        return value.time
    }
}