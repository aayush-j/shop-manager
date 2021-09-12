package com.rttc.shopmanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rttc.shopmanager.R
import com.rttc.shopmanager.ShopApplication
import com.rttc.shopmanager.adapter.EntryListAdapter
import com.rttc.shopmanager.adapter.EntryListListener
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : Fragment(), EntryListListener {

    @Inject
    lateinit var entryRepository: EntryRepository

    companion object {
        const val ARG_ENTRY_ID = "com.rttc.shopmanager.arg.ENTRY_ID"
    }

    private val homeViewModel by activityViewModels<HomeViewModel> {
        Instances.provideHomeViewModelFactory(entryRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ShopApplication).shopComponent.inject(this)
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
                    val bottomSheet = FilterBottomSheet()
                    bottomSheet.show(parentFragmentManager, "filter_sheet")
                }

                R.id.actionOptions -> findNavController().navigate(R.id.action_homeFragment_to_appPreferences)

                R.id.actionSearch -> findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            true
        }

        val entryListAdapter = EntryListAdapter(requireContext(), this)
        rvHomeItems.apply {
            adapter = entryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        homeViewModel.entryList.observe(viewLifecycleOwner, { list ->
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
            findNavController().navigate(R.id.action_homeFragment_to_modifyFragment)
        }
    }

    override fun onItemClick(entryId: Long) {
        val bundle = Bundle()
        bundle.putLong(ARG_ENTRY_ID, entryId)
        findNavController().navigate(R.id.action_homeFragment_to_entryFragment, bundle)
    }
}
