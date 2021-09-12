package com.rttc.shopmanager

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rttc.shopmanager.utilities.PREF_ONBOARDING
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        (application as ShopApplication).shopComponent.inject(this)

        val intent = if (!sharedPreferences.contains(PREF_ONBOARDING)) {
            Intent(this, OnBoardingActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
