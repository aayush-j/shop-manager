package com.rttc.shopmanager.onboarding

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rttc.shopmanager.MainActivity
import com.rttc.shopmanager.R
import com.rttc.shopmanager.ShopApplication
import com.rttc.shopmanager.utilities.DatabaseHelperActivity
import com.rttc.shopmanager.utilities.LOG
import com.rttc.shopmanager.utilities.OSUtils
import com.rttc.shopmanager.utilities.PREF_ONBOARDING
import kotlinx.android.synthetic.main.fragment_welcome.*
import javax.inject.Inject

class WelcomeFragment : Fragment() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireContext().applicationContext as ShopApplication).shopComponent.inject(this)

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                LOG(result.toString())
                if (result.resultCode == Activity.RESULT_OK) {
                    sharedPreferences
                        .edit()
                        .putBoolean(PREF_ONBOARDING, true)
                        .apply()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnWelcomeLoadBackup?.isEnabled = OSUtils.isDataBackupAvailable()

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
        btnWelcomeCategory?.startAnimation(animation)
        btnWelcomeLoadBackup?.startAnimation(animation)

        btnWelcomeCategory?.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_categoryFragmentWelcome)
        }

        btnWelcomeLoadBackup?.setOnClickListener {
            val intent = Intent(context, DatabaseHelperActivity::class.java)
            intent.putExtra(
                DatabaseHelperActivity.EXTRA_ACTION_TYPE,
                DatabaseHelperActivity.ACTION_RESTORE
            )
            activityResultLauncher.launch(intent)
        }
    }
}