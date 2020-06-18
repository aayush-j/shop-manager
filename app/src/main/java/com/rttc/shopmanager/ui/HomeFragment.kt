package com.rttc.shopmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rttc.shopmanager.R
import com.rttc.shopmanager.adapter.EntryListAdapter
import com.rttc.shopmanager.adapter.EntryListListener
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), EntryListListener, SearchFilterListener {

    companion object {
        const val ARG_ENTRY_ID = "com.rttc.shopmanager.arg.ENTRY_ID"
    }

    private val homeViewModel by viewModels<HomeViewModel> {
        Instances.provideHomeViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeActionBar?.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.actionFilterResults -> {
                    val bottomSheet = FilterBottomSheet(
                        this,
                        homeViewModel.enquiryType.value,
                        homeViewModel.statusType.value
                    )
                    bottomSheet.show(parentFragmentManager, "filter_sheet")
                }

                R.id.actionOptions -> NavHostFragment
                    .findNavController(this)
                    .navigate(R.id.action_homeFragment_to_appPreferences)

            }
            true
        }

        val entryListAdapter = EntryListAdapter(requireContext(), this)
        rvHomeItems.apply {
            adapter = entryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        homeViewModel.entryList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                entryListAdapter.setItems(it)
                homeActionBar?.title =
                    if (it.isNotEmpty()) "Showing ${list.size} items"
                    else "No items added"

                tvWelcomeText.visibility =
                    if (it.isEmpty()) View.VISIBLE
                    else View.GONE
            }
        })

        fabNewEntry?.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_homeFragment_to_modifyFragment)
        }
    }

    override fun onItemClick(entryId: Long) {
        val bundle = Bundle()
        bundle.putLong(ARG_ENTRY_ID, entryId)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_homeFragment_to_entryFragment, bundle)
    }

    override fun applyFilter(enquiryType: String, status: String) {
        homeViewModel.enquiryType.value = enquiryType
        homeViewModel.statusType.value = status
    }
}
