package com.rttc.shopmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Category

class CategoryListAdapter(appContext: Context, private val listener: CategoryListListener)
    : ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    private var inflater: LayoutInflater = LayoutInflater.from(appContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val rootView = inflater.inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvCategoryName)
        private val btnDelete = itemView.findViewById<ImageView>(R.id.btnDeleteCategory)

        fun bindView(category: Category) {
            tvTitle.text = category.title
            btnDelete.setOnClickListener {
                listener.onButtonClick(category)
            }
        }
    }

    class CategoryDiffCallback: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.title == newItem.title
        }
    }
}