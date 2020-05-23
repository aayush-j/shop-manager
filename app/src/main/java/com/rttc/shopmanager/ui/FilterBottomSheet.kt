package com.rttc.shopmanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.rttc.shopmanager.R
import com.rttc.shopmanager.utilities.LOG_PREFIX
import com.rttc.shopmanager.utilities.TYPE_ALL
import kotlinx.android.synthetic.main.bottom_sheet_filter.*

class FilterBottomSheet(
    private val filterListener: SearchFilterListener,
    private var enquiryType: String?,
    private var statusType: String?
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addViewsToTypeSelector()
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
            filterListener.applyFilter(enquiryType?: TYPE_ALL, statusType?: TYPE_ALL)
            dismiss()
        }

        btnResetFilter.setOnClickListener {
            filterListener.applyFilter(TYPE_ALL, TYPE_ALL)
            dismiss()
        }
    }

    private fun addViewsToTypeSelector() {
        val types = arrayListOf<String>()
        types.addAll(requireContext().resources.getStringArray(R.array.enquiry_types))
        var i = 100
        types.forEach {
            val chip = createChipWithId(i++)
            chip.text = it
            cgTypeSelector.addView(chip)
            if (it.equals(enquiryType, true))
                chip.isChecked = true
        }

        val status = arrayOf(ModifyFragment.STATUS_OPEN, ModifyFragment.STATUS_CLOSED)
        status.forEach {
            val chip = createChipWithId(i++)
            chip.text = it
            cgStatusSelector.addView(chip)
            if (it.equals(statusType, true))
                chip.isChecked = true
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