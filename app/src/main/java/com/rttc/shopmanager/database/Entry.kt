package com.rttc.shopmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "register")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    var name: String,
    @ColumnInfo(name = "primary_contact")
    var primaryContact: String,
    @ColumnInfo(name = "secondary_contact")
    var secondaryContact: String,
    @ColumnInfo(name = "whatsApp_contact")
    var whatsAppContact: String,
    var email: String,
    var address: String,
    @ColumnInfo(name = "enquiry_type")
    var enquiryType: String,
    @ColumnInfo(name = "enquiry_details")
    var enquiryDetails: String,
    var status: String,
    @ColumnInfo(name = "date_opened") @TypeConverters(DateConverter::class)
    var dateOpened: Date?,
    @ColumnInfo(name = "date_closed") @TypeConverters(DateConverter::class)
    var dateClosed: Date?
) {
    constructor(
        name: String = "",
        primaryContact: String = "",
        secondaryContact: String = "",
        whatsAppContact: String = "",
        email: String = "",
        address: String = "",
        enquiryType: String = "",
        enquiryDetails: String = "",
        status: String = "",
        dateOpened: Date? = null,
        dateClosed: Date? = null) : this(0, name, primaryContact, secondaryContact, whatsAppContact, email, address, enquiryType, enquiryDetails, status, dateOpened, dateClosed)
}