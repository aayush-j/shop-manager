package com.rttc.shopmanager.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.LOG_PREFIX
import com.rttc.shopmanager.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.entry_enquiry_card.*
import kotlinx.android.synthetic.main.entry_options_card.*
import kotlinx.android.synthetic.main.entry_personal_card.*
import kotlinx.android.synthetic.main.fragment_entry.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class EntryFragment : Fragment(), View.OnClickListener {

    private val entryViewModel by viewModels<EntryViewModel> {
        Instances.provideEntryViewModelFactory(requireContext(), arguments?.getLong(HomeFragment.ARG_ENTRY_ID) ?: -1)
    }

    private var recEntry: Entry? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entryActionBar?.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }

        setButtonListeners()
        entryViewModel.entry.observe(viewLifecycleOwner, Observer { entry ->
            if (entry != null) {
                recEntry = entry
                populateUi(entry)
            }
        })
    }

    private fun setButtonListeners() {
        btnDelete.setOnClickListener(this)
        btnEdit.setOnClickListener(this)
        btnShare.setOnClickListener(this)
        btnCloseEnquiry.setOnClickListener(this)

        tvEntryContactPrim.setOnClickListener(this)
        tvEntryContactSec.setOnClickListener(this)
        btnWhatsAppPrim.setOnClickListener(this)
        btnWhatsAppSec.setOnClickListener(this)
        tvEntryEmail.setOnClickListener(this)
    }

    private fun populateUi(entry: Entry) {
        entry.let {
            entryActionBar?.title = it.name
            tvEntryAddress.text = it.address
            tvEntryContactPrim.text = it.primaryContact
            tvEntryType.text = it.enquiryType
            val sdf = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
            val dateOpened = "Opened on ${sdf.format(it.dateOpened!!)}"
            tvEntryOpenDate.text = dateOpened
            tvEntryStatus.text = it.status

            if (!TextUtils.isEmpty(it.email))
                makeViewsVisible(tvEntryEmail, it.email)

            if (!TextUtils.isEmpty(it.secondaryContact))
                makeViewsVisible(tvEntryContactSec, it.secondaryContact)

            when (it.whatsAppContact) {
                ModifyFragment.WHATSAPP_PRIM -> {
                    btnWhatsAppPrim.visibility = View.VISIBLE
                    btnWhatsAppSec.visibility = View.GONE
                }
                ModifyFragment.WHATSAPP_SEC -> {
                    btnWhatsAppPrim.visibility = View.GONE
                    btnWhatsAppSec.visibility = View.VISIBLE
                }
                else -> {
                    btnWhatsAppPrim.visibility = View.GONE
                    btnWhatsAppSec.visibility = View.GONE
                }
            }

            if (it.status == ModifyFragment.STATUS_CLOSED){
                makeViewsVisible(tvEntryClosedDate, "Closed on ${sdf.format(it.dateClosed!!)}")
                tvEntryStatus.background = requireContext().getDrawable(R.drawable.enquiry_closed_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorClosedStroke))
                btnCloseEnquiry.text = "Reopen Enquiry"
            }
            else{
                makeViewsGone(tvEntryClosedDate)
                tvEntryStatus.background = requireContext().getDrawable(R.drawable.enquiry_open_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorOpenStroke))
                btnCloseEnquiry.text = "Close Enquiry"
            }

            if (!TextUtils.isEmpty(it.enquiryDetails))
                makeViewsVisible(tvEntryDetail, it.enquiryDetails)
        }
    }

    private fun makeViewsVisible(textView: TextView?, tvText: String) {
        textView?.visibility = View.VISIBLE
        textView?.text = tvText
    }

    private fun makeViewsGone(textView: TextView?) {
        textView?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        recEntry?.let {
            when(v?.id) {
                R.id.btnDelete -> deleteEntry(it)

                R.id.btnShare -> shareDetails(it)

                R.id.btnEdit -> {
                    val bundle = Bundle()
                    bundle.putLong(HomeFragment.ARG_ENTRY_ID, it.id)
                    this.findNavController().navigate(R.id.action_entryFragment_to_modifyFragment, bundle)
                }

                R.id.tvEntryEmail -> emailCustomer(it)

                R.id.tvEntryContactPrim -> callCustomer(it.primaryContact)

                R.id.tvEntryContactSec -> callCustomer(it.secondaryContact)

                R.id.btnWhatsAppPrim -> whatsAppCustomer(it.primaryContact)

                R.id.btnWhatsAppSec -> whatsAppCustomer(it.secondaryContact)

                R.id.btnCloseEnquiry -> {
                    if (it.status == ModifyFragment.STATUS_OPEN) {
                        btnCloseEnquiry.text = "Reopen Enquiry"
                        it.status = ModifyFragment.STATUS_CLOSED
                        Calendar.getInstance().let {calendar ->
                            calendar.timeZone = TimeZone.getTimeZone("IST")
                            it.dateClosed = calendar.time
                        }
                    }
                    else {
                        btnCloseEnquiry.text = "Close Enquiry"
                        it.status = ModifyFragment.STATUS_OPEN
                    }
                    entryViewModel.updateEntry(it)
                }

                else -> Log.d(LOG_PREFIX, "No button pressed")
            }
        }
    }

    private fun displayNumberSelectorDialog() {
        recEntry?.let {
            val numbers = arrayOf(it.primaryContact, it.secondaryContact)
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Please select a number")
                .setItems(numbers) { _, which ->
                    callCustomer(numbers[which])
                }
                .show()
        }
    }

    private fun deleteEntry(entry: Entry) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete enquiry")
            .setMessage("Do you want to delete this enquiry")
            .setNegativeButton("No") { _, _ ->

            }
            .setPositiveButton("Yes") { _, _ ->
                entryViewModel.deleteEntry(entry)
                this.findNavController().popBackStack()
            }
            .show()
    }

    private fun whatsAppCustomer(numberToSend: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("https://wa.me/91$numberToSend")
        }
        if (sendIntent.resolveActivity(requireContext().packageManager) != null) {
            try {
                startActivity(sendIntent)
            }
            catch (e: Exception) {
                displayToast("WhatsApp is not installed")
            }
        }
    }

    private fun callCustomer(contactNumber: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$contactNumber")
        }
        try {
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        catch (e: Exception) {
            Log.d(LOG_PREFIX, e.message ?: "emailCustomer() unknown exception")
        }
    }

    private fun emailCustomer(it: Entry) {
        if (!TextUtils.isEmpty(it.email)) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                data = Uri.parse("mailto:")
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(it.email))
                putExtra(Intent.EXTRA_SUBJECT, arrayOf("Regarding Enquiry for ${it.enquiryType}"))
                putExtra(Intent.EXTRA_TEXT, "Thank you for your enquiry")
            }
            try {
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
            catch (e: Exception) {
                Log.d(LOG_PREFIX, e.message ?: "emailCustomer() unknown exception")
            }
        }
        else {
            displayToast("Please add an email address")
            return
        }
    }

    private fun shareDetails(it: Entry) {
        val contact = it.primaryContact

        if (!TextUtils.isEmpty(it.secondaryContact))
            contact.plus(", ").plus(it.secondaryContact)

        val message = "Enquiry for ${it.enquiryType}\nName: ${it.name}\nContact: $contact\nAddress: ${it.address}"

        if (!TextUtils.isEmpty(it.enquiryDetails))
            message.plus("\nDetails: ${it.enquiryDetails}")

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        try {
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
        catch (e: Exception) {
            Log.d(LOG_PREFIX, e.message ?: "emailCustomer() unknown exception")
        }
    }

    private fun displayToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
