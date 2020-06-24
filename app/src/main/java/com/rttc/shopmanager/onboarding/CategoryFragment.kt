package com.rttc.shopmanager.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rttc.shopmanager.MainActivity
import com.rttc.shopmanager.R
import com.rttc.shopmanager.adapter.CategoryListAdapter
import com.rttc.shopmanager.adapter.CategoryListListener
import com.rttc.shopmanager.database.Category
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.PREFS_NAME
import com.rttc.shopmanager.utilities.PREF_ONBOARDING
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
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        categoryViewModel.categories.observe(viewLifecycleOwner, Observer { list ->
            categoryListAdapter.submitList(list)
        })

        btnAddCategory?.setOnClickListener {
            if (!TextUtils.isEmpty(etCategoryName.text.toString())) {
                categoryViewModel.insert(Category(etCategoryName.text.toString().trim()))
                etCategoryName.setText("")
            }
            else
                Toast.makeText(requireContext(), "Please enter a category name", Toast.LENGTH_SHORT).show()
        }

        val preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!preferences.contains(PREF_ONBOARDING))
            btnCompleteOnBoarding?.visibility = View.VISIBLE
        else
            btnCompleteOnBoarding?.visibility = View.GONE

        btnCompleteOnBoarding?.setOnClickListener {
            preferences.edit()
                .putBoolean(PREF_ONBOARDING, true)
                .apply()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onButtonClick(category: Category) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning")
            .setMessage("All enquiries of ${category.title} will also be deleted. Do you still want to proceed?")
            .setNegativeButton("No") { _, _ ->

            }
            .setPositiveButton("Yes") { _, _ ->
                categoryViewModel.delete(category)
            }
            .show()
    }
}