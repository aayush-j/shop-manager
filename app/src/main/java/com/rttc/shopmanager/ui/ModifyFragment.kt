package com.rttc.shopmanager.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rttc.shopmanager.R
import com.rttc.shopmanager.database.Entry
import com.rttc.shopmanager.database.ShopDatabase
import com.rttc.shopmanager.utilities.Instances
import com.rttc.shopmanager.utilities.SelfTesting
import com.rttc.shopmanager.viewmodel.ModifyViewModel
import kotlinx.android.synthetic.main.fragment_modify.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ModifyFragment : Fragment() {

    private val modifyViewModel by viewModels<ModifyViewModel> {
        Instances.provideModifyViewModelFactory(requireContext(), arguments?.getLong(HomeFragment.ARG_ENTRY_ID) ?: -1)
    }

    companion object {
        const val WHATSAPP_PRIM = "primary"
        const val WHATSAPP_SEC = "secondary"
        const val WHATSAPP_NONE = "none"
        const val STATUS_OPEN = "open"
        const val STATUS_CLOSED = "closed"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_modify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(IO).launch {
            setCategoriesToSpinner()
        }

        modifyActionBar?.setNavigationOnClickListener {
            NavHostFragment.findNavController(this).popBackStack()
        }

        if (PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getBoolean(getString(R.string.pref_testing_enabled), false))
            modifyActionBar?.inflateMenu(R.menu.testing_menu)
        else
            modifyActionBar?.menu

        modifyActionBar?.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.actionRandomData -> setupData(SelfTesting.getRandomEntry())
            }
            true
        }

        var newEntry = Entry()
        modifyViewModel.getEntry().observe(viewLifecycleOwner, Observer{entry ->
            if (entry != null) {
                modifyActionBar?.title = "Edit enquiry"
                setupData(entry)
                newEntry = entry
            }
            else
                modifyActionBar?.title = "New enquiry"
        })

        etPrimContact.doOnTextChanged { text, _, _, _ ->
            if (text?.length!! != 10) {
                displayError(tilPrimContact, true)
            }
            else {
                displayError(tilPrimContact, false)
            }
        }

        etSecContact.doOnTextChanged { text, _, _, _ ->
            if (text?.length!! != 10 && text.isNotEmpty()) {
                displayError(tilSecContact, true)
            }
            else {
                displayError(tilSecContact, false)
            }
        }

        etEmail.doOnTextChanged { text, _, _, _ ->
            if (text?.isNotEmpty()!! && !android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                displayError(tilEmail, true)
            }
            else {
                displayError(tilEmail, false)
            }
        }

        btnAddCategoryExt.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_modifyFragment_to_categoryFragment)
        }
        
        btnSaveDetails.setOnClickListener {
            if (isDataValid() && isRadioValid()) {
                newEntry.apply {
                    name = etName.text.toString().trim()
                    address = etAddress.text.toString().trim()
                    primaryContact = etPrimContact.text.toString().trim()
                    secondaryContact = etSecContact.text.toString().trim()
                    whatsAppContact = when {
                        rbWhatsAppPrim.isChecked -> WHATSAPP_PRIM
                        rbWhatsAppSec.isChecked -> WHATSAPP_SEC
                        else -> WHATSAPP_NONE
                    }
                    email = etEmail.text.toString().trim()
                    enquiryType = spinnerEnquiryType.text.toString()
                    enquiryDetails = etEnquiryDetail.text.toString().trim()
                }
                if (arguments != null) {
                    modifyViewModel.updateEntry(newEntry)
                    Toast.makeText(requireContext(), "Enquiry Updated", Toast.LENGTH_SHORT).show()
                }
                else {
                    newEntry.status = STATUS_OPEN
                    val calendar = Calendar.getInstance()
                    calendar.timeZone = TimeZone.getTimeZone("IST")
                    newEntry.dateOpened = calendar.time
                    modifyViewModel.insertEntry(newEntry)
                    Toast.makeText(requireContext(), "Enquiry Added", Toast.LENGTH_SHORT).show()
                }
                it.findNavController().popBackStack()

                hideSoftKeyboard(it)
            }
        }
    }

    private suspend fun setCategoriesToSpinner() {
        val categories = getCategoriesFromDb()
        withContext(Dispatchers.Main) {
            addCategoriesToSpinner(categories)
        }
    }

    private fun addCategoriesToSpinner(categories: List<String>) {
        if (categories.isNotEmpty()) {
            val arrayAdapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                categories
            )
            spinnerEnquiryType?.isEnabled = true
            spinnerEnquiryType?.setAdapter(arrayAdapter)
            spinnerEnquiryType?.setText(categories[0], false)
        }
        else {
            spinnerEnquiryType?.isEnabled = false
        }
    }

    private fun getCategoriesFromDb(): List<String> {
        return ShopDatabase.getInstance(requireContext()).entryDao().getCategoryList()
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    private fun isRadioValid(): Boolean {
        return if (rbWhatsAppSec.isChecked && TextUtils.isEmpty(etSecContact.text.toString())) {
            tilSecContact.isErrorEnabled = true
            tilSecContact.error = "${tilSecContact.hint} cannot be empty for WhatsApp contact"
            false
        } else {
            tilSecContact.isErrorEnabled = false
            true
        }
    }

    private fun setupData(entry: Entry) {
        etName.setText(entry.name)
        etAddress.setText(entry.address)
        etPrimContact.setText(entry.primaryContact)
        etSecContact.setText(entry.secondaryContact)
        when (entry.whatsAppContact) {
            WHATSAPP_PRIM -> rbWhatsAppPrim.isChecked = true
            WHATSAPP_SEC -> rbWhatsAppSec.isChecked = true
        }
        etEmail.setText(entry.email)
        spinnerEnquiryType.setText(entry.enquiryType, false)
        etEnquiryDetail.setText(entry.enquiryDetails)
    }


    private fun isDataValid(): Boolean {
        if (isTextEmpty(etName, tilName)
            || isTextEmpty(etAddress, tilAddress)
            || isSpinnerEmpty(spinnerEnquiryType, tilEnquiryType)
            || !isEmailValid()
            || !isPrimaryContactValid()
            || !isSecondaryContactValid()
        )
            return false
        return true
    }

    private fun isEmailValid(): Boolean {
        val email = etEmail.editableText.toString()
        return if (email.isEmpty() || isEmailAddressValid(email)) {
            displayError(tilEmail, false)
            true
        } else {
            displayError(tilEmail, true)
            false
        }
    }

    private fun isEmailAddressValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPrimaryContactValid(): Boolean {
        val number = etPrimContact.editableText.toString()
        return if (number.length != 10) {
            displayError(tilPrimContact, true)
            false
        } else {
            displayError(tilPrimContact, false)
            true
        }
    }

    private fun isSecondaryContactValid(): Boolean {
        val number = etSecContact.editableText.toString()
        return if (number.length != 10 && number.isNotEmpty()) {
            displayError(tilSecContact, true)
            false
        } else {
            displayError(tilSecContact, false)
            true
        }
    }

    private fun isSpinnerEmpty(spinnerEnquiryType: AutoCompleteTextView?, tilEnquiryType: TextInputLayout?): Boolean {
        if (TextUtils.isEmpty(spinnerEnquiryType?.editableText.toString())) {
            displayError(tilEnquiryType, true)
            return true
        }
        return false
    }

    private fun isTextEmpty(editText: TextInputEditText?, textInputLayout: TextInputLayout?): Boolean {
        return if (TextUtils.isEmpty(editText?.editableText.toString())) {
            displayError(textInputLayout, true)
            scrollToError(textInputLayout)
            true
        } else {
            displayError(textInputLayout, false)
            false
        }
    }

    private fun displayError(textInputLayout: TextInputLayout?, option: Boolean) {
        if (option) {
            textInputLayout?.isErrorEnabled = true
            textInputLayout?.error = "${textInputLayout?.hint} is invalid"
        }
        else
            textInputLayout?.isErrorEnabled = false
    }

    private fun scrollToError(errorView: TextInputLayout?) {
        errorView?.let {
            svFragmentModify.smoothScrollTo(errorView.scrollX, errorView.scrollY)
        }
    }
}
