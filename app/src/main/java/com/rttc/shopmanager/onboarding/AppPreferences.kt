package com.rttc.shopmanager.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.rttc.shopmanager.BuildConfig
import com.rttc.shopmanager.R
import com.rttc.shopmanager.SplashActivity
import com.rttc.shopmanager.utilities.DatabaseHelperActivity
import com.rttc.shopmanager.utilities.LOG
import com.rttc.shopmanager.utilities.OSUtils

class AppPreferences : PreferenceFragmentCompat() {

    private var backupPref: Preference? = null
    private var restorePref: Preference? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                LOG(result.toString())
                if (result.resultCode == Activity.RESULT_OK) {
                    startActivity(Intent(requireContext(), SplashActivity::class.java))
                    requireActivity().finish()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(requireContext().getColor(R.color.colorWhite))
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_pref, rootKey)

        val categoryPref = findPreference<Preference>(getString(R.string.pref_manage_category))
        categoryPref?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_appPreferences_to_categoryFragment)
            true
        }

        backupPref = findPreference(getString(R.string.pref_create_backup))
        backupPref?.setOnPreferenceClickListener {
            val intent = Intent(context, DatabaseHelperActivity::class.java)
            intent.putExtra(
                DatabaseHelperActivity.EXTRA_ACTION_TYPE,
                DatabaseHelperActivity.ACTION_BACKUP
            )
            startActivity(intent)
            true
        }

        backupPref?.setOnPreferenceChangeListener { preference, newValue ->
            preference.summary = newValue as String
            true
        }

        restorePref = findPreference<Preference>(getString(R.string.pref_restore_backup))
        restorePref?.setOnPreferenceClickListener {
            val intent = Intent(context, DatabaseHelperActivity::class.java)
            intent.putExtra(
                DatabaseHelperActivity.EXTRA_ACTION_TYPE,
                DatabaseHelperActivity.ACTION_RESTORE
            )
            activityResultLauncher.launch(intent)
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

    override fun onResume() {
        super.onResume()
        updatePreferencesState()
    }

    private fun updatePreferencesState() {
        backupPref?.isEnabled = OSUtils.isDataBackupAvailable()
        restorePref?.isEnabled = OSUtils.isDataBackupAvailable()
    }

    private fun reportBug() {
        var msg = "Please retain the information below\n" +
                "App Version: ${BuildConfig.VERSION_NAME}\n" +
                "Version Code: ${BuildConfig.VERSION_CODE}\n"
        try {
            msg = msg + "OS Version: ${Build.VERSION.RELEASE}\n" +
                    "Device: ${Build.BRAND} ${Build.MODEL}\n" +
                    "Manufacturer: ${Build.MANUFACTURER}\n\n" +
                    "Your thoughts: "
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("aayushjain.y16@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "[ShopManager] Bug found")
            putExtra(Intent.EXTRA_TEXT, msg)
        }
        startActivity(Intent.createChooser(sendIntent, "Please choose an email client"))
    }
}