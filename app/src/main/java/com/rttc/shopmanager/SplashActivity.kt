package com.rttc.shopmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rttc.shopmanager.utilities.PREFS_NAME
import com.rttc.shopmanager.utilities.PREF_ONBOARDING

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!preferences.contains(PREF_ONBOARDING))
            startActivity(Intent(this, OnBoardingActivity::class.java))
        else
            startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
