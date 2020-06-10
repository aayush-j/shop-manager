package com.rttc.shopmanager.ui

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.rttc.shopmanager.R
import com.rttc.shopmanager.adapter.CategoryListAdapter
import com.rttc.shopmanager.adapter.CategoryListListener
import com.rttc.shopmanager.database.Category
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.fragment_category.*

class CategoryFragment : Fragment(), CategoryListListener {

    private val categoryViewModel by viewModels<CategoryViewModel> {
        Instances.provideCategoryViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryListAdapter = CategoryListAdapter(requireContext(), this)
        rvCategoryItems.apply {
            adapter = categoryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        categoryViewModel.categories.observe(viewLifecycleOwner, Observer { list ->
            categoryListAdapter.setItems(list)
        })

        btnAddCategory?.setOnClickListener {
            if (!TextUtils.isEmpty(etCategoryName.text.toString()))
                categoryViewModel.insert(Category(etCategoryName.text.toString().trim()))
            else
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onButtonClick(category: Category) {
        categoryViewModel.delete(category)
    }
}