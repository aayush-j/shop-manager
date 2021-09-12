package com.rttc.shopmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.rttc.shopmanager.R
import com.rttc.shopmanager.ShopApplication
import com.rttc.shopmanager.database.Category
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.TYPE_ALL
import com.rttc.shopmanager.viewmodel.CategoryViewModel
import com.rttc.shopmanager.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.bottom_sheet_filter.*
import javax.inject.Inject

private val TAG = FilterBottomSheet::class.simpleName

class FilterBottomSheet : BottomSheetDialogFragment() {

    @Inject
    lateinit var entryRepository: EntryRepository

    private val categoryViewModel by viewModels<CategoryViewModel> {
        Instances.provideCategoryViewModelFactory(entryRepository)
    }

    private val homeViewModel by activityViewModels<HomeViewModel> {
        Instances.provideHomeViewModelFactory(entryRepository)
    }

    private lateinit var enquiryType: String
    private lateinit var statusType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ShopApplication).shopComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryViewModel.categories.observe(this, { categories ->
            categories?.let {
                addViewsToTypeSelector(it)
            }
        })

        homeViewModel.enquiryType.observe(this, {
            enquiryType = it
        })

        homeViewModel.statusType.observe(this, {
            statusType = it
        })

        cgTypeSelector.setOnCheckedChangeListener { _, checkedId ->
            enquiryType = if (checkedId == -1) {
                TYPE_ALL
            } else {
                val chip = view.findViewById<Chip>(checkedId)
                chip.text.toString()
            }
        }

        cgStatusSelector.setOnCheckedChangeListener { _, checkedId ->
            statusType = if (checkedId == -1) {
                TYPE_ALL
            } else {
                val chip = view.findViewById<Chip>(checkedId)
                chip.text.toString()
            }
        }

        btnApplyFilter.setOnClickListener {
            homeViewModel.applyFilter(enquiryType, statusType)
            dismiss()
        }

        btnResetFilter.setOnClickListener {
            homeViewModel.applyFilter(TYPE_ALL, TYPE_ALL)
            dismiss()
        }
    }

    private fun addViewsToTypeSelector(categories: List<Category>) {
        var i = 100
        categories.forEach {
            val chip = createChipWithId(i++)
            chip.text = it.title
            cgTypeSelector.addView(chip)
            chip.isChecked = it.title.equals(enquiryType, true)
        }

        val status = arrayOf(ModifyFragment.STATUS_OPEN, ModifyFragment.STATUS_CLOSED)
        status.forEach {
            val chip = createChipWithId(i++)
            chip.text = it
            cgStatusSelector.addView(chip)
            chip.isChecked = it.equals(statusType, true)
        }
    }

    private fun createChipWithId(chipId: Int): Chip {
        val chip = Chip(requireContext())
        chip.apply {
            id = chipId
            isCheckable = true
            setTextColor(requireContext().getColor(R.color.colorTypeStroke))
            setChipBackgroundColorResource(R.color.colorTypeBg)
            setCheckedIconTintResource(R.color.colorTypeStroke)
        }
        return chip
    }
}