package com.rttc.shopmanager.onboarding

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import com.rttc.shopmanager.MainActivity
import com.rttc.shopmanager.R
import com.rttc.shopmanager.SplashActivity
import com.rttc.shopmanager.utilities.DatabaseHelper
import com.rttc.shopmanager.utilities.PREFS_NAME
import com.rttc.shopmanager.utilities.PREF_ONBOARDING
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

        btnWelcomeLoadBackup?.setOnClickListener {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                when (DatabaseHelper.restoreLocalBackup(requireContext())) {
                    DatabaseHelper.SUCCESS -> {
                        showToast("Restore successful")
                        val preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        preferences.edit()
                            .putBoolean(PREF_ONBOARDING, true)
                            .apply()

                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                    DatabaseHelper.DIR_NA -> showToast("Backup files not found")
                    else -> showToast("Failed to restore data")
                }
            }
            else {
                showToast("Please provide storage permissions")
            }
        }
    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

}