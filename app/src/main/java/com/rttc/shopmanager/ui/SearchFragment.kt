package com.rttc.shopmanager.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rttc.shopmanager.R
import com.rttc.shopmanager.adapter.EntryListAdapter
import com.rttc.shopmanager.adapter.EntryListListener
import com.rttc.shopmanager.database.EntryDao
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.database.ShopDatabase
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), EntryListListener {

    lateinit var entryDao: EntryDao
    lateinit var entryListAdapter: EntryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryDao = ShopDatabase.getInstance(requireContext()).entryDao()
        entryListAdapter = EntryListAdapter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivCloseSearchFragment?.setOnClickListener {
            it.findNavController().popBackStack()
        }

        rvSearchItems.apply {
            adapter = entryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        etSearchQuery?.doOnTextChanged { text, _, _, _ ->
            searchDb(text?.trim())
        }
    }

    private fun searchDb(text: CharSequence?) {
        val query = "%$text%"
        entryDao.searchByName(query).observe(viewLifecycleOwner, Observer {list ->
            list?.let {
                entryListAdapter.setItems(it)
            }
        })
    }

    override fun onItemClick(entryId: Long) {
        val bundle = Bundle()
        bundle.putLong(HomeFragment.ARG_ENTRY_ID, entryId)
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_searchFragment_to_entryFragment, bundle)
    }
}