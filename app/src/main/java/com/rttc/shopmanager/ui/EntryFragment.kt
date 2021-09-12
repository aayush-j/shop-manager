package com.rttc.shopmanager.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rttc.shopmanager.R
import com.rttc.shopmanager.ShopApplication
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.EntryRepository
import com.rttc.shopmanager.utilities.ENTRY_DATE_FORMAT
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.LOG
import com.rttc.shopmanager.utilities.toastMessage
import com.rttc.shopmanager.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.entry_enquiry_card.*
import kotlinx.android.synthetic.main.entry_options_card.*
import kotlinx.android.synthetic.main.entry_personal_card.*
import kotlinx.android.synthetic.main.fragment_entry.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class EntryFragment : Fragment(), View.OnClickListener {

    @Inject
    lateinit var entryRepository: EntryRepository

    private val entryViewModel by viewModels<EntryViewModel> {
        Instances.provideEntryViewModelFactory(entryRepository)
    }

    private val clipBoardCopyListener = View.OnLongClickListener {
        val clipBoard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = (it as TextView).text
        val clipData = ClipData.newPlainText("Content", text)
        clipBoard.setPrimaryClip(clipData)
        toastMessage("Copied to clipboard")
        true
    }

    private lateinit var recEntry: Entry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as ShopApplication).shopComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        entryViewModel.entId.value = arguments?.getLong(HomeFragment.ARG_ENTRY_ID) ?: -1
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        entryActionBar?.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        setButtonListeners()
        setCopyToClipboardListeners()
        entryViewModel.entry.observe(viewLifecycleOwner, { entry ->
            entry?.let {
                recEntry = it
                populateUi()
            }
        })
    }

    private fun setCopyToClipboardListeners() {
        tvEntryAddress.setOnLongClickListener(clipBoardCopyListener)
        tvEntryContactPrim.setOnLongClickListener(clipBoardCopyListener)
        tvEntryContactSec.setOnLongClickListener(clipBoardCopyListener)
        tvEntryEmail.setOnLongClickListener(clipBoardCopyListener)
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

    private fun populateUi() {
        recEntry.let {
            entryActionBar?.title = it.name
            tvEntryAddress.text = it.address
            tvEntryContactPrim.text = it.primaryContact
            tvEntryType.text = it.enquiryType
            val sdf = SimpleDateFormat(ENTRY_DATE_FORMAT, Locale.US)
            val dateOpened = "Opened on ${sdf.format(it.dateOpened!!)}"

            tvEntryOpenDate.text = dateOpened
            tvEntryStatus.text = it.status

            if (!TextUtils.isEmpty(it.email)) {
                setVisibleWithText(tvEntryEmail, it.email)
            }

            if (!TextUtils.isEmpty(it.secondaryContact)) {
                setVisibleWithText(tvEntryContactSec, it.secondaryContact)
            }

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

            if (it.status == ModifyFragment.STATUS_CLOSED) {
                setVisibleWithText(tvEntryClosedDate, "Closed on ${sdf.format(it.dateClosed!!)}")
                tvEntryStatus.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.enquiry_closed_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorClosedStroke))
                btnCloseEnquiry.text = "Reopen Enquiry"
            } else {
                setViewGone(tvEntryClosedDate)
                tvEntryStatus.background =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.enquiry_open_tag)
                tvEntryStatus.setTextColor(requireContext().getColor(R.color.colorOpenStroke))
                btnCloseEnquiry.text = "Close Enquiry"
            }

            if (!TextUtils.isEmpty(it.enquiryDetails)) {
                setVisibleWithText(tvEntryDetail, it.enquiryDetails)
            }
        }
    }

    private fun setVisibleWithText(textView: TextView?, tvText: String) {
        textView?.visibility = View.VISIBLE
        textView?.text = tvText
    }

    private fun setViewGone(textView: TextView?) {
        textView?.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        recEntry.let {
            when (v?.id) {
                R.id.btnDelete -> deleteEntry(it)

                R.id.btnShare -> shareDetails(it)

                R.id.btnEdit -> {
                    val bundle = Bundle()
                    bundle.putLong(HomeFragment.ARG_ENTRY_ID, it.id)
                    findNavController().navigate(
                        R.id.action_entryFragment_to_modifyFragment,
                        bundle
                    )
                }

                R.id.tvEntryEmail -> emailCustomer(it.email, it.enquiryType)

                R.id.tvEntryContactPrim -> callCustomer(it.primaryContact)

                R.id.tvEntryContactSec -> callCustomer(it.secondaryContact)

                R.id.btnWhatsAppPrim -> whatsAppCustomer(it.primaryContact)

                R.id.btnWhatsAppSec -> whatsAppCustomer(it.secondaryContact)

                R.id.btnCloseEnquiry -> {
                    if (it.status == ModifyFragment.STATUS_OPEN) {
                        btnCloseEnquiry.text = "Reopen Enquiry"
                        entryViewModel.closeEntry(it)
                    } else {
                        btnCloseEnquiry.text = "Close Enquiry"
                        entryViewModel.openEntry(it)
                    }
                }

                else -> LOG("No button pressed")
            }
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
                findNavController().popBackStack()
            }
            .show()
    }

    private fun whatsAppCustomer(numberToSend: String) {
        val whatsAppUrl = "https://api.whatsapp.com/send?phone=+91$numberToSend"
        if (isPackageInstalled("com.whatsapp")) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(whatsAppUrl)
            startActivity(intent)
        } else {
            toastMessage("WhatsApp is not installed")
        }
    }

    private fun isPackageInstalled(packageName: String): Boolean {
        try {
            requireContext().packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_ACTIVITIES
            )
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            LOG("$packageName not found!!")
        }
        return false
    }

    private fun callCustomer(contactNumber: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$contactNumber")
        }
        try {
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        } catch (e: Exception) {
            LOG(e.message ?: "callCustomer() unknown exception")
        }
    }

    private fun emailCustomer(email: String, enquiryType: String) {
        if (!TextUtils.isEmpty(email)) {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, "Re: Enquiry regarding $enquiryType")
                putExtra(Intent.EXTRA_TEXT, "Thank you for contacting us.")
            }
            try {
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            } catch (e: Exception) {
                LOG(e.message ?: "emailCustomer() unknown exception")
            }
        } else {
            toastMessage("Please add an email address")
            return
        }
    }

    private fun shareDetails(it: Entry) {
        val contact = it.primaryContact

        if (!TextUtils.isEmpty(it.secondaryContact))
            contact.plus(", ").plus(it.secondaryContact)

        val message =
            "Enquiry for ${it.enquiryType}\nName: ${it.name}\nContact: $contact\nAddress: ${it.address}"

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
        } catch (e: Exception) {
            LOG(e.message ?: "shareDetails() unknown exception")
        }
    }
}
