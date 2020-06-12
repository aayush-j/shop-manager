package com.rttc.shopmanager.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.rttc.shopmanager.R
import kotlinx.android.synthetic.main.fragment_options.*

class OptionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnManageCategory?.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_optionsFragment_to_categoryFragment)
        }

        btnBackupAndRestore?.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_optionsFragment_to_dataManagerFragment)
        }
    }
}