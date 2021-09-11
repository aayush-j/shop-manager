package com.rttc.shopmanager.utilities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.rttc.shopmanager.R
import com.rttc.shopmanager.ShopApplication
import com.rttc.shopmanager.database.ShopDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DatabaseHelperActivity : AppCompatActivity() {
    @Inject
    lateinit var shopDatabase: ShopDatabase

    private val mimeType = "application/octet-stream"

    companion object {
        const val EXTRA_ACTION_TYPE = "action_type"
        const val ACTION_BACKUP = "backup"
        const val ACTION_RESTORE = "restore"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_helper)

        (this.applicationContext as ShopApplication).shopComponent.inject(this)

        intent?.let {
            when (it.getStringExtra(EXTRA_ACTION_TYPE)) {
                ACTION_BACKUP -> startBackupProcess()
                ACTION_RESTORE -> startRestoreProcess()
                else -> LOG("Action not found in received intent!!")
            }
        }
    }

    private fun startBackupProcess() {
        val nameDateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US)
        val fileName = "shop_manager_backup_${nameDateFormat.format(Date())}.dtt"
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.data?.let { uri ->
                        startBackup(uri)
                    }
                } else {
                    finish()
                    toastMessage("Failed to create file")
                }
            }
        activityResultLauncher.launch(intent)
    }

    private fun startRestoreProcess() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            type = mimeType
        }
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.data?.let { uri ->
                        startRestore(uri)
                    }
                } else {
                    finish()
                    toastMessage("Failed to get file")
                }
            }
        activityResultLauncher.launch(intent)
    }

    private fun startRestore(restoreFileUri: Uri) {
        shopDatabase.close()
        var result = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dbFile = File(shopDatabase.openHelper.writableDatabase.path)
                val dbFileUri = Uri.fromFile(dbFile)
                val inputStream = contentResolver.openInputStream(restoreFileUri)
                val outputStream = contentResolver.openOutputStream(dbFileUri)
                result = DatabaseHelper.copyFileStreams(inputStream!!, outputStream!!)
                withContext(Dispatchers.Main) {
                    if (result) {
                        LOG("restore file copied successfully!")
                        toastMessage("Data restored successfully")
                    } else {
                        toastMessage("Failed to restore data")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                setResult(if (result) Activity.RESULT_OK else Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun startBackup(backupFileUri: Uri) {
        shopDatabase.close()
        var result = false
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val dbFile = File(shopDatabase.openHelper.writableDatabase.path)
                val dbFileUri = Uri.fromFile(dbFile)
                val inputStream = contentResolver.openInputStream(dbFileUri)
                val outputStream = contentResolver.openOutputStream(backupFileUri)
                result = DatabaseHelper.copyFileStreams(inputStream!!, outputStream!!)
                withContext(Dispatchers.Main) {
                    if (result) {
                        LOG("backup file copied successfully!")
                        toastMessage("Data backup successful")
                    } else {
                        toastMessage("Failed to backup data")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                setResult(if (result) Activity.RESULT_OK else Activity.RESULT_CANCELED)
                finish()
            }
        }
    }
}