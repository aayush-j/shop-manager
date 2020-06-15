package com.rttc.shopmanager.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryLite
import com.rttc.shopmanager.ui.ModifyFragment
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import com.rttc.shopmanager.utilities.ITEM_DATE_FORMAT
import com.rttc.shopmanager.utilities.LOG_PREFIX
import java.text.SimpleDateFormat
import java.util.*

class EntryListAdapter(private val appContext: Context, private val entryListener: EntryListListener)
    :RecyclerView.Adapter<EntryListAdapter.EntryViewHolder>() {

    private var entries = emptyList<Entry>()
    private val inflater: LayoutInflater = LayoutInflater.from(appContext)

    fun setItems(entryList: List<Entry>) {
        this.entries = entryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val rootView = inflater.inflate(R.layout.entry_item, parent, false)
        return EntryViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.bindView(entries[position])
    }

    inner class EntryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
        private val tvDateAdded = itemView.findViewById<TextView>(R.id.tvItemDateAdded)
        private val tvType = itemView.findViewById<TextView>(R.id.tvItemType)
        private val viewType = itemView.findViewById<View>(R.id.viewItemType)

        fun bindView(entry: Entry) {
            tvName.text = entry.name
            tvType.text = entry.enquiryType
            tvDateAdded.text = "+91 ${entry.primaryContact}"
            /*Log.d(LOG_PREFIX, "Recyclerview item date = ${entry.dateOpened}")
            entry.dateOpened?.let {
                val simpleDateFormat = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
                simpleDateFormat.timeZone = TimeZone.getTimeZone("IST")
                tvDateAdded.text = simpleDateFormat.format(it.time)
            }*/

            viewType.setBackgroundColor(
                if (entry.status == ModifyFragment.STATUS_OPEN)
                    appContext.getColor(R.color.colorOpenStroke)
                else
                    appContext.getColor(R.color.colorClosedStroke)
            )

            itemView.setOnClickListener {
                entryListener.onItemClick(entry.id)
            }
        }
    }
}