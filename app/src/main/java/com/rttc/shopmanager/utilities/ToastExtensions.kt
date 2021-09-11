package com.rttc.shopmanager.utilities

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Activity.toastMessage(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun Fragment.toastMessage(message: String) =
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()