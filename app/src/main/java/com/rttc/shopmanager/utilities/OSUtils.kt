package com.rttc.shopmanager.utilities

import android.os.Build

object OSUtils {
    fun isDataBackupAvailable() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}