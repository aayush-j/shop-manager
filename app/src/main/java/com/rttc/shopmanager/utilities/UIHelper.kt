package com.rttc.shopmanager.utilities

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.rttc.shopmanager.R

object UIHelper {
    fun setStatusBarLight(activity: AppCompatActivity) {
        val window = activity.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.statusBarColor = activity.getColor(R.color.colorWhite)
    }
}