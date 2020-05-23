package com.rttc.shopmanager.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager

import com.rttc.shopmanager.R
import com.rttc.shopmanager.adapter.EntryListAdapter
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.LOG_PREFIX
import com.rttc.shopmanager.utilities.SelfTesting
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

        val entryListAdapter = EntryListAdapter(requireContext(), this)
        rvHomeItems.apply {
            adapter = entryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
            //val animID = R.anim.layout_anim_from_bottom
            //layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), animID)
        }

        homeViewModel.entryList.observe(viewLifecycleOwner, Observer { list ->
            list?.let {
                entryListAdapter.setItems(it)
                val textToDisplay = "Showing ${list.size} enquiries"
                tvTotalEnquiries?.text = textToDisplay
                tvWelcomeText.visibility =
                    if (it.isEmpty()) View.VISIBLE
                    else View.GONE
                }
        })

        fabNewEntry?.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_modifyFragment)
        }

        fabAddTestEntries?.setOnClickListener {
            val newEntries = mutableListOf<Entry>()
            for (i in 1..20) {
                newEntries.add(SelfTesting.getRandomEntry())
            }
            homeViewModel.insertAllEnquiries(newEntries.toList())
            Toast.makeText(requireContext(), "Added 20 more test entries", Toast.LENGTH_SHORT).show()
        }

        btnOpenFilter?.setOnClickListener {
            val bottomSheet = FilterBottomSheet(
                this,
                homeViewModel.enquiryType.value,
                homeViewModel.statusType.value
            )
            bottomSheet.show(parentFragmentManager, "filter_sheet")
        }
    }

    override fun onItemClick(entryId: Long) {
        val bundle = Bundle()
        bundle.putLong(ARG_ENTRY_ID, entryId)
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFragment_to_entryFragment, bundle)
    }

    private fun runAnimation() {
        rvHomeItems.apply {
            val animID = R.anim.layout_anim_from_bottom
            layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), animID)
            adapter?.notifyDataSetChanged()
            scheduleLayoutAnimation()
        }
    }

    override fun applyFilter(enquiryType: String, status: String) {
        homeViewModel.enquiryType.value = enquiryType
        homeViewModel.statusType.value = status
    }
}
