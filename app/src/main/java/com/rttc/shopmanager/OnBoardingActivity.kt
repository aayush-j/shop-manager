package com.rttc.shopmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rttc.shopmanager.utilities.DatabaseHelper

class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        DatabaseHelper.createBackupDirectory(this)
    }
}