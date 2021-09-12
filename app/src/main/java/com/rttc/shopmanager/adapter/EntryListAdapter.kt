package com.rttc.shopmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.EntryLite
import com.rttc.shopmanager.ui.ModifyFragment
import com.rttc.shopmanager.utilities.ITEM_DATE_FORMAT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class EntryListAdapter(
    private val appContext: Context,
    private val entryListener: EntryListListener
) : ListAdapter<EntryLite, RecyclerView.ViewHolder>(EntryAdapterDiffCallback()) {

    companion object {
        const val ITEM_TYPE_ENTRY = 1
        const val ITEM_TYPE_FOOTER = 2
    }

    private val inflater: LayoutInflater = LayoutInflater.from(appContext)

    fun setItems(entryList: List<EntryLite>) {
        CoroutineScope(Dispatchers.Default).launch {
            val items =
                if (entryList.isNotEmpty())
                    entryList + listOf(EntryLite(-1, "", "", "", null, ITEM_TYPE_FOOTER))
                else
                    entryList
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).item_type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_ENTRY) {
            val view = inflater.inflate(R.layout.entry_item, parent, false)
            EntryViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.home_rv_footer, parent, false)
            FooterViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_ENTRY)
            (holder as EntryViewHolder).bindView(getItem(position))
    }

    inner class EntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvItemName)
        private val tvDateAdded = itemView.findViewById<TextView>(R.id.tvItemDateAdded)
        private val tvType = itemView.findViewById<TextView>(R.id.tvItemType)

        fun bindView(entry: EntryLite) {
            tvName.text = entry.name
            tvType.text = entry.enquiry_type

            entry.date_opened?.let {
                val simpleDateFormat = SimpleDateFormat(ITEM_DATE_FORMAT, Locale.US)
                tvDateAdded.text = simpleDateFormat.format(it.time)
            }

            if (entry.status == ModifyFragment.STATUS_CLOSED) {
                tvName.setTextColor(appContext.getColor(R.color.colorClosedStroke))
                tvType.background =
                    AppCompatResources.getDrawable(appContext, R.drawable.enquiry_closed_tag)
                tvType.setTextColor(appContext.getColor(R.color.colorClosedStroke))
            } else {
                tvName.setTextColor(appContext.getColor(R.color.colorOnSurface))
                tvType.background =
                    AppCompatResources.getDrawable(appContext, R.drawable.item_type_tag)
                tvType.setTextColor(appContext.getColor(R.color.colorTypeStroke))
            }

            itemView.setOnClickListener {
                entryListener.onItemClick(entry.id)
            }
        }
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EntryAdapterDiffCallback : DiffUtil.ItemCallback<EntryLite>() {
        override fun areItemsTheSame(oldItem: EntryLite, newItem: EntryLite): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EntryLite, newItem: EntryLite): Boolean {
            return oldItem == newItem
        }
    }
}