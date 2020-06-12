package com.rttc.shopmanager.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.rttc.shopmanager.MainActivity
import com.rttc.shopmanager.R
import com.rttc.shopmanager.utilities.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_welcome.*
import java.io.IOException

class WelcomeFragment : Fragment() {
    companion object {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        btnWelcomeCategory?.startAnimation(animation)

        btnWelcomeCategory?.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_welcomeFragment_to_categoryFragmentWelcome)
        }
    }
}