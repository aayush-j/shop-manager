package com.rttc.shopmanager.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.ShopDatabase
import com.rttc.shopmanager.utilities.DatabaseHelper
import kotlinx.android.synthetic.main.fragment_data_manager.*


class DataManagerFragment : Fragment() {
    companion object {
        private const val RESTORE_REQUEST_CODE = 213
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_manager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(
//            Intent.createChooser(intent, "Choose backup file"),
//            RESTORE_REQUEST_CODE
//        )

        btnRestoreBackup?.setOnClickListener {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                if (DatabaseHelper.restoreLocalBackup(requireContext())){
                    showToast("Restore successful. App will automatically closed.")
                    requireActivity().finish()
                }
                else
                    showToast("Restore failed")
            }
            else {
                showToast("Please provide storage permissions")
            }
        }

        btnCreateBackup?.setOnClickListener {
            val permission = ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                if (DatabaseHelper.createLocalBackup(requireContext()))
                    showToast("Backup created")
                else
                    showToast("Backup failed")
            }
            else {
                showToast("Please provide storage permissions")
            }
        }

    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESTORE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val fileUri = data.data
            try {
                fileUri?.let {
                    val inputStream = requireContext().contentResolver.openInputStream(it)
                    val result = DatabaseHelper.restoreDatabase(requireContext(), inputStream)
                    inputStream?.close()
                    if (!result)
                        Toast.makeText(requireContext(), "Restore failed", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(requireContext(), "Successfully restored", Toast.LENGTH_SHORT).show()
                        //startActivity(Intent(requireContext(), MainActivity::class.java))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/
}