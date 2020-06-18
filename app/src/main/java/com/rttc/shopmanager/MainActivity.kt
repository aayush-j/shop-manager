package com.rttc.shopmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rttc.shopmanager.utilities.DatabaseHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //UIHelper.setStatusBarLight(this)
        DatabaseHelper.verifyStoragePermissions(this)
    }
}
