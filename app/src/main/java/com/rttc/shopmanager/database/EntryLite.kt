package com.rttc.shopmanager.database

import androidx.room.Ignore
import com.rttc.shopmanager.adapter.EntryListAdapter
import java.util.*

data class EntryLite(
    var id: Long,
    var name: String,
    var enquiry_type: String,
    var status: String,
    var date_opened: Date?,
    @Ignore var item_type: Int = EntryListAdapter.ITEM_TYPE_ENTRY
) {
    constructor() : this(0, "", "", "", null)
}