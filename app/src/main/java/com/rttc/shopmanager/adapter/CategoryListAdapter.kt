package com.rttc.shopmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Category

class CategoryListAdapter(private val appContext: Context, private val listener: CategoryListListener)
    : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    private var categories = emptyList<Category>()
    private var inflater: LayoutInflater = LayoutInflater.from(appContext)

    fun setItems(categoryList: List<Category>) {
        this.categories = categoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val rootView = inflater.inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindView(categories[position])
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
}