package com.rttc.shopmanager.database

import java.util.*

data class EntryLite(
    var id: Long,
    var name: String,
    var enquiry_type: String,
    var status: String,
    var date_opened: Date?
)