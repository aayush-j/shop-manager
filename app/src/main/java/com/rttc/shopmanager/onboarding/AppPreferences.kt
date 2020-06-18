package com.rttc.shopmanager.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rttc.shopmanager.BuildConfig
import com.rttc.shopmanager.R
import com.rttc.shopmanager.SplashActivity
import com.rttc.shopmanager.utilities.DatabaseHelper
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

class AppPreferences: PreferenceFragmentCompat() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColor(R.color.colorWhite))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_pref, rootKey)

        val preferences = preferenceManager.sharedPreferences

        val categoryPref = findPreference<Preference>(getString(R.string.pref_manage_category))
        categoryPref?.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_appPreferences_to_categoryFragment)
            true
        }

        val backupPref = findPreference<Preference>(getString(R.string.pref_create_backup))
        backupPref?.summary = preferences.getString(backupPref?.key, "No backup created")
        backupPref?.setOnPreferenceClickListener {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Warning")
                    .setMessage("All previous backups will be replaced by current backup")
                    .setPositiveButton("Proceed") { _, _ ->
                        initBackup(it)
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .show()
            }
            else {
                showToast("Please provide storage permissions")
            }
            true
        }

        backupPref?.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as String
            true
        }

        val restorePref = findPreference<Preference>(getString(R.string.pref_restore_backup))
        restorePref?.setOnPreferenceClickListener {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Warning")
                    .setMessage("Your current data will be replaced by backup data")
                    .setPositiveButton("Proceed") { _, _ ->
                        initRestore()
                    }
                    .setNegativeButton("Cancel") { _, _ ->

                    }
                    .show()
            }
            else {
                showToast("Please provide storage permissions")
            }

            true
        }

        val versionPref = findPreference<Preference>(getString(R.string.pref_app_version))
        versionPref?.summary = BuildConfig.VERSION_NAME

        val bugPref = findPreference<Preference>(getString(R.string.pref_report_bug))
        bugPref?.setOnPreferenceClickListener {
            reportBug()
            true
        }
    }

    private fun initRestore() {
        when (DatabaseHelper.restoreLocalBackup(requireContext())) {
            DatabaseHelper.SUCCESS -> {
                showToast("Restore successful")
                startActivity(Intent(requireContext(), SplashActivity::class.java))
                requireActivity().finish()
            }
            DatabaseHelper.DIR_NA -> showToast("Backup files not found")
            else -> showToast("Failed to restore data")
        }
    }

    private fun initBackup(preference: Preference) {
        if (DatabaseHelper.createLocalBackup(requireContext()) == DatabaseHelper.SUCCESS) {
            showToast("Backup created")
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("IST")
            val sdf = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
            val summary = "Last backup created on ${sdf.format(calendar.time)}"
            preference.sharedPreferences
                .edit()
                .putString(getString(R.string.pref_create_backup), summary)
                .apply()
            preference.summary = summary
        }
        else
            showToast("Backup failed")
    }

    private fun reportBug() {
        var msg = "Please retain the information below\n" +
                "App Version: ${BuildConfig.VERSION_NAME}\n" +
                "Version Code: ${BuildConfig.VERSION_CODE}\n"
        try {
            msg = msg + "OS Version: ${Build.VERSION.RELEASE}\n" +
                    "Device: ${Build.BRAND} ${Build.MODEL}\n" +
                    "Manufacturer: ${Build.MANUFACTURER}\n\n" +
                    "Bug found:\n"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("aayushjain.y16@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "[ShopManager] Bug Report")
            putExtra(Intent.EXTRA_TEXT, msg)
        }
        startActivity(Intent.createChooser(sendIntent, "Please choose an email client"))
    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}