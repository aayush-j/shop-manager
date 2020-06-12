package com.rttc.shopmanager.utilities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.FileUtils
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
        const val BACKUP_NEW = "backup_type_new"
        const val BACKUP_RESTORE = "backup_type_restore"
        private const val DB_TEMP = "backup_restore_temp"

        fun verifyStoragePermissions(appCompatActivity: AppCompatActivity) {
            val requestCode = 1;
            val storagePermissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val readPermission = ActivityCompat.checkSelfPermission(
                appCompatActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val writePermission = ActivityCompat.checkSelfPermission(
                appCompatActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            if (readPermission != PackageManager.PERMISSION_GRANTED || writePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    appCompatActivity,
                    storagePermissions,
                    requestCode
                )
            }
        }

        fun createBackupDirectory(context: Context) {
            val appDir = File(ContextCompat.getExternalFilesDirs(context, null)[0], APP_DIR)
            if (!appDir.exists()){
                if (!appDir.mkdir())
                    Log.d(LOG_PREFIX, "Unable to create directory")
                else
                    Log.d(LOG_PREFIX, "Backup directory created successfully")
            }
            else
                Log.d(LOG_PREFIX, "Backup directory already exist")
        }

        fun createLocalBackup(context: Context): Boolean {
            ShopDatabase.getInstance(context).close()
            val db = context.getDatabasePath(DB_NAME)
            val dbShm = File(db.parent!!, DB_NAME.plus("-shm"))
            val dbWal = File(db.parent!!, DB_NAME.plus("-wal"))

            val appDir = File(ContextCompat.getExternalFilesDirs(context, null)[0], APP_DIR)
            createBackupDirectory(context)
            val dbBackup = File(appDir.absolutePath, DB_NAME)
            val dbShmBackup = File(dbBackup.parent!!, DB_NAME.plus("-shm"))
            val dbWalBackup = File(dbBackup.parent!!, DB_NAME.plus("-wal"))

            try {
                copy(db, dbBackup)
                copy(dbShm, dbShmBackup)
                copy(dbWal, dbWalBackup)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        fun restoreLocalBackup(context: Context): Boolean {
            ShopDatabase.getInstance(context).close()
            val db = context.getDatabasePath(DB_NAME)
            val dbShm = File(db.parent!!, DB_NAME.plus("-shm"))
            val dbWal = File(db.parent!!, DB_NAME.plus("-wal"))

            val appDir = File(ContextCompat.getExternalFilesDirs(context, null)[0], APP_DIR)
            createBackupDirectory(context)
            val dbBackup = File(appDir.absolutePath, DB_NAME)
            val dbShmBackup = File(dbBackup.parent!!, DB_NAME.plus("-shm"))
            val dbWalBackup = File(dbBackup.parent!!, DB_NAME.plus("-wal"))

            try {
                copy(dbBackup, db)
                copy(dbShmBackup, dbShm)
                copy(dbWalBackup, dbWal)
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
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

        fun backupDatabase(context: Context, backupType: String): Boolean {
            val shopDatabase = ShopDatabase.getInstance(context)
            shopDatabase.close()
            val db = context.getDatabasePath(DB_NAME)
            val appDir = File(ContextCompat.getExternalFilesDirs(context, null)[0], APP_DIR)
            val filePath =
                if (backupType == BACKUP_NEW)
                    appDir.path + File.separator + "backup_${System.currentTimeMillis()}"
                else
                    appDir.path + File.separator + DB_TEMP

            Log.d(LOG_PREFIX, "Filepath: ${appDir.absolutePath}")
            if (!appDir.exists())
                createBackupDirectory(context)

            val newFile = File(filePath)
            if (newFile.exists())
                newFile.delete()

            try {
                newFile.createNewFile()
                val bufferSize = 8 * 1024
                val buffer = ByteArray(bufferSize)
                val saveDb = FileOutputStream(filePath)
                val inpDb = FileInputStream(db)
                var bytesRead = inpDb.read(buffer, 0, bufferSize)
                while (bytesRead > 0) {
                    saveDb.write(buffer, 0, bytesRead)
                    bytesRead = inpDb.read(buffer, 0, bufferSize)
                }
                saveDb.flush()
                inpDb.close()
                saveDb.close()
                Log.d(LOG_PREFIX, "Successfully created at $filePath")
                Toast.makeText(context, "Backup created at $filePath", Toast.LENGTH_SHORT)
                    .show()
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        fun restoreDatabase(context: Context, inputStream: InputStream?): Boolean {
            val shopDatabase = ShopDatabase.getInstance(context)
            shopDatabase.close()
            if (!backupDatabase(context, BACKUP_RESTORE))
                return false

            val currDb = context.getDatabasePath(DB_NAME)
            inputStream?.let {
                try {
                    createCopy(inputStream as FileInputStream, FileOutputStream(currDb))
                    return true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return false
        }

        private fun createCopy(srcStream: FileInputStream, destStream: FileOutputStream) {
            var srcChannel: FileChannel? = null
            var destChannel: FileChannel? = null
            try {
                srcChannel = srcStream.channel
                destChannel = destStream.channel
                srcChannel?.transferTo(0, srcChannel.size(), destChannel)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    srcChannel?.close()
                } finally {
                    destChannel?.close()
                }
            }
        }
    }
}