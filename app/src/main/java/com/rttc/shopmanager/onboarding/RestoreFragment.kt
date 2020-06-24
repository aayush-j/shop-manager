package com.rttc.shopmanager.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.rttc.shopmanager.MainActivity
import com.rttc.shopmanager.R
import com.rttc.shopmanager.utilities.DatabaseHelper
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import com.rttc.shopmanager.utilities.PREFS_NAME
import com.rttc.shopmanager.utilities.PREF_ONBOARDING
import kotlinx.android.synthetic.main.fragment_restore.*
import java.text.SimpleDateFormat
import java.util.*

class RestoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backupFile = DatabaseHelper.getBackupFile()
        if (backupFile != null) {
            val sdf = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
            val msg = "Backup found\nCreated: ${sdf.format(backupFile.lastModified())}"
            tvRestoreFiles.text = msg
        }
        else {
            tvRestoreFiles.text = "Backup not found\nMake sure you have ShopManagerBackup directory (with all the three files) inside your emulated storage"
            btnStartRestore.text = "Go back"
            btnStartRestore.icon = requireContext().getDrawable(R.drawable.ic_back)
        }

        btnStartRestore?.setOnClickListener {
            if (backupFile == null)
                it.findNavController().popBackStack()
            else {
                if (DatabaseHelper.isStoragePermissionsGranted(requireContext())) {
                    when (DatabaseHelper.restoreLocalBackup(requireContext())) {
                        DatabaseHelper.SUCCESS -> {
                            showToast("Successfully restored")
                            requireActivity()
                                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                .edit()
                                .putBoolean(PREF_ONBOARDING, true)
                                .apply()
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            requireActivity().finish()
                        }

                        else -> {
                            showToast("Failed to restore data. Try again by going to Settings.")
                            it.findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

}