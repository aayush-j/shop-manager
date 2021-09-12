package com.rttc.shopmanager.utilities

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

fun Fragment.LOG(logMessage: String) {
    addLog(this::class.simpleName!!, logMessage)
}

fun Activity.LOG(logMessage: String) {
    addLog(this::class.simpleName!!, logMessage)
}

fun ViewModel.LOG(logMessage: String) {
    addLog(this::class.simpleName!!, logMessage)
}

private fun addLog(tag: String, message: String) =
    Log.d(tag, message)

