package com.rttc.shopmanager.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.rttc.shopmanager.R
import com.rttc.shopmanager.utilities.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_welcome.*

class WelcomeFragment : Fragment() {

    companion object {
        const val STORAGE_REQUEST_CODE = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        btnWelcomeCategory?.startAnimation(animation)
        btnWelcomeLoadBackup?.startAnimation(animation)

        btnWelcomeCategory?.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_welcomeFragment_to_categoryFragmentWelcome)
        }

        btnWelcomeLoadBackup?.setOnClickListener {
            val storagePermissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (DatabaseHelper.isStoragePermissionsGranted(requireContext()))
                it.findNavController().navigate(R.id.action_welcomeFragment_to_restoreFragment)
            else
                requestPermissions(storagePermissions, STORAGE_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    NavHostFragment.findNavController(this).navigate(R.id.action_welcomeFragment_to_restoreFragment)
                else
                    showToast("Please provide storage permissions")
            }
        }
    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

}