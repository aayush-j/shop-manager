package com.rttc.shopmanager.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rttc.shopmanager.database.ShopDatabase
import java.io.*
import java.nio.channels.FileChannel

class DatabaseHelper {
    companion object {
        const val SUCCESS = "backup_success"
        const val FAILED = "backup_failed"
        const val DIR_NA = "directory_not_available"

        fun isStoragePermissionsGranted(context: Context): Boolean {
            val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (readPermission == PackageManager.PERMISSION_GRANTED && writePermission == PackageManager.PERMISSION_GRANTED)
                return true
            return false
        }

        fun createBackupDirectory(): File? {
            val appDir = File(Environment.getExternalStorageDirectory(), APP_DIR)
            //val appDir = File(ContextCompat.getExternalFilesDirs(context, null)[0], APP_DIR)
            if (!appDir.exists()){
                if (!appDir.mkdir()) {
                    Log.d(LOG_PREFIX, "Unable to create directory")
                    return null
                }
                else {
                    Log.d(LOG_PREFIX, "Backup directory created at ${appDir.absolutePath}")
                }
            }
            else
                Log.d(LOG_PREFIX, "Backup directory already exist")
            return appDir
        }

        fun createLocalBackup(context: Context): String {
            ShopDatabase.getInstance(context).close()
            val db = context.getDatabasePath(DB_NAME)
            val dbShm = File(db.parent!!, DB_NAME.plus("-shm"))
            val dbWal = File(db.parent!!, DB_NAME.plus("-wal"))

            val appDir = createBackupDirectory()
            appDir?.let {
                val dbBackup = File(it.absolutePath, DB_NAME)
                val dbShmBackup = File(dbBackup.parent!!, DB_NAME.plus("-shm"))
                val dbWalBackup = File(dbBackup.parent!!, DB_NAME.plus("-wal"))

                try {
                    copy(db, dbBackup)
                    copy(dbShm, dbShmBackup)
                    copy(dbWal, dbWalBackup)
                    Log.d(LOG_PREFIX, "Backup created at ${it.absolutePath}")
                    return SUCCESS
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return FAILED
        }

        fun restoreLocalBackup(context: Context): String {
            ShopDatabase.getInstance(context).close()
            val db = context.getDatabasePath(DB_NAME)
            val dbShm = File(db.parent!!, DB_NAME.plus("-shm"))
            val dbWal = File(db.parent!!, DB_NAME.plus("-wal"))

            val appDir = createBackupDirectory()
            appDir?.let {
                val dbBackup = File(it.absolutePath, DB_NAME)
                val dbShmBackup = File(dbBackup.parent!!, DB_NAME.plus("-shm"))
                val dbWalBackup = File(dbBackup.parent!!, DB_NAME.plus("-wal"))
                if (!dbBackup.exists() || !dbShmBackup.exists() || !dbWalBackup.exists())
                    return DIR_NA

                try {
                    copy(dbBackup, db)
                    copy(dbShmBackup, dbShm)
                    copy(dbWalBackup, dbWal)
                    return SUCCESS
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return FAILED
        }

        fun getBackupFile(): File? {
            val appDir = createBackupDirectory()
            appDir?.let {
                val dbBackup = File(it.absolutePath, DB_NAME)
                val dbShmBackup = File(dbBackup.parent!!, DB_NAME.plus("-shm"))
                val dbWalBackup = File(dbBackup.parent!!, DB_NAME.plus("-wal"))
                if (dbBackup.exists() && dbShmBackup.exists() && dbWalBackup.exists())
                    return dbBackup
            }
            return null
        }

        @Throws(IOException::class)
        fun copy(src: File, dst: File) {
            FileInputStream(src).use { inp ->
                FileOutputStream(dst).use { out ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inp.read(buf).also { len = it } > 0) {
                        out.write(buf, 0, len)
                    }
                }
            }
        }
    }
}