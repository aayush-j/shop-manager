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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.LOG_PREFIX
import com.rttc.shopmanager.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.entry_enquiry_card.*
import kotlinx.android.synthetic.main.entry_personal_card.*
import kotlinx.android.synthetic.main.fragment_entry.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class EntryFragment : Fragment(), View.OnClickListener {

    private val entryViewModel by viewModels<EntryViewModel> {
        Instances.provideEntryViewModelFactory(requireContext(), arguments?.getLong(HomeFragment.ARG_ENTRY_ID) ?: -1)
    }

    var recEntry: Entry? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        entryViewModel.entry.observe(viewLifecycleOwner, Observer { entry ->
            if (entry != null) {
                recEntry = entry
                populateUi(entry)
            }
        })
    }

    private fun setButtonListeners() {
        btnCallPrim.setOnClickListener(this)
        btnDelete.setOnClickListener(this)
        btnEdit.setOnClickListener(this)
        btnEmail.setOnClickListener(this)
        btnShare.setOnClickListener(this)
        btnSendWhatsApp.setOnClickListener(this)
        btnCloseEnquiry.setOnClickListener(this)
    }

    private fun populateUi(entry: Entry) {
        entry.let {
            tvEntryName.text = it.name
            tvEntryAddress.text = it.address
            tvEntryContactPrim.text = it.primaryContact
            tvEntryType.text = it.enquiryType
            val sdf = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
            val dateOpened = "Opened on ${sdf.format(it.dateOpened!!)}"
            tvEntryOpenDate.text = dateOpened
            tvEntryStatus.text = it.status

            if (!TextUtils.isEmpty(it.email))
                makeViewsVisible(ivEntryEmail, tvEntryEmail, it.email)

            if (!TextUtils.isEmpty(it.secondaryContact)) {
                tvEntryContactSec.visibility = View.VISIBLE
                tvEntryContactSec.text = it.secondaryContact
            }

            if (it.status == ModifyFragment.STATUS_CLOSED){
                makeViewsVisible(ivEntryClosedDate, tvEntryClosedDate, "Closed on ${sdf.format(it.dateClosed!!)}")
                tvEntryStatus.background = requireContext().getDrawable(R.drawable.enquiry_closed_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorClosedStroke))
                btnCloseEnquiry.text = "Reopen Enquiry"
            }
            else{
                makeViewsGone(ivEntryClosedDate, tvEntryClosedDate)
                tvEntryStatus.background = requireContext().getDrawable(R.drawable.enquiry_open_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorOpenStroke))
                btnCloseEnquiry.text = "Close Enquiry"
            }

            if (!TextUtils.isEmpty(it.enquiryDetails))
                makeViewsVisible(ivEntryDetail, tvEntryDetail, it.enquiryDetails)
        }
    }

    private fun makeViewsVisible(imageView: ImageView?, textView: TextView?, tvText: String) {
        imageView?.visibility = View.VISIBLE
        textView?.visibility = View.VISIBLE
        textView?.text = tvText
    }

    private fun makeViewsGone(imageView: ImageView?, textView: TextView?) {
        imageView?.visibility = View.GONE
        textView?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnDelete -> deleteEntry()

            R.id.btnShare -> shareDetails()

            R.id.btnEdit ->
                recEntry?.let {
                    val bundle = Bundle()
                    bundle.putLong(HomeFragment.ARG_ENTRY_ID, it.id)
                    this.findNavController().navigate(R.id.action_entryFragment_to_modifyFragment, bundle)
                }

            R.id.btnEmail -> emailCustomer()

            R.id.btnCallPrim -> recEntry?.let {
                if (TextUtils.isEmpty(it.secondaryContact))
                    callCustomer(it.primaryContact)
                else
                    displayNumberSelectorDialog()
            }

            R.id.btnSendWhatsApp -> whatsAppCustomer()

            R.id.btnCloseEnquiry -> recEntry?.let {
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

    private fun deleteEntry() {
        recEntry?.let {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete enquiry")
                .setMessage("Do you want to delete this enquiry")
                .setNegativeButton("No") { _, _ ->

                }
                .setPositiveButton("Yes") { _, _ ->
                    entryViewModel.deleteEntry(it)
                    this.findNavController().popBackStack()
                }
                .show()
        }
    }

    private fun whatsAppCustomer() {
        recEntry?.let {
            if (it.whatsAppContact == ModifyFragment.WHATSAPP_NONE
                || (it.whatsAppContact == ModifyFragment.WHATSAPP_SEC
                        && TextUtils.isEmpty(it.secondaryContact))) {
                displayToast("Please add a WhatsApp contact")
                return
            }

            val numberToSend: String =
                if (it.whatsAppContact == ModifyFragment.WHATSAPP_PRIM) it.primaryContact
                else it.secondaryContact

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

    private fun emailCustomer() {
        recEntry?.let {
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
    }

    private fun shareDetails() {
        recEntry?.let {
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
    }

    private fun displayToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
