package com.example.firstaidapp.ui.contacts

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.data.models.PhoneContact
import com.example.firstaidapp.databinding.ActivityPhoneContactsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhoneContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneContactsBinding
    private lateinit var viewModel: ContactsViewModel
    private lateinit var adapter: PhoneContactsAdapter

    private var phoneContacts = mutableListOf<PhoneContact>()
    private var filteredContacts = mutableListOf<PhoneContact>()
    private var selectedContacts = mutableSetOf<String>()

    private val requestContactsPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadPhoneContacts()
        } else {
            // Distinguish between rationale vs permanent denial
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                showContactsRationaleDialog()
            } else {
                showPermissionPermanentlyDeniedDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupToolbar()
        setupRecyclerView()
        setupSearchFunctionality()
        setupClickListeners()

        checkPermissionAndLoadContacts()
    }

    private fun setupViewModel() {
        val factory = ContactsViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[ContactsViewModel::class.java]
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = PhoneContactsAdapter { contact, isSelected ->
            handleContactSelection(contact, isSelected)
        }

        binding.rvPhoneContacts.apply {
            layoutManager = LinearLayoutManager(this@PhoneContactsActivity)
            adapter = this@PhoneContactsActivity.adapter
        }
    }

    private fun setupSearchFunctionality() {
        binding.etSearchContacts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupClickListeners() {
        binding.btnAddSelected.setOnClickListener {
            addSelectedContacts()
        }
    }

    private fun checkPermissionAndLoadContacts() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadPhoneContacts()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                showContactsRationaleDialog()
            }
            else -> {
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showContactsRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.contacts_permission_rationale)
            .setPositiveButton(R.string.allow) { _, _ ->
                requestContactsPermission.launch(Manifest.permission.READ_CONTACTS)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_required)
            .setMessage(R.string.contacts_permission_permanently_denied)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                openAppSettings()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }

    private fun loadPhoneContacts() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val contacts = mutableListOf<PhoneContact>()
                val resolver: ContentResolver = contentResolver
                val cursor: Cursor? = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
                )

                cursor?.use {
                    val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)

                    while (it.moveToNext()) {
                        val name = it.getString(nameIndex) ?: ""
                        val number = it.getString(numberIndex) ?: ""
                        val id = it.getString(idIndex) ?: ""

                        if (name.isNotEmpty() && number.isNotEmpty()) {
                            contacts.add(
                                PhoneContact(
                                    id = id,
                                    name = name,
                                    phoneNumber = number.replace("[^+\\d]".toRegex(), "")
                                )
                            )
                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    phoneContacts = contacts
                    filteredContacts = phoneContacts.toMutableList()
                    adapter.submitList(filteredContacts.toList())
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, "Error loading contacts: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun filterContacts(query: String) {
        filteredContacts = if (query.isEmpty()) {
            phoneContacts.toMutableList()
        } else {
            phoneContacts.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.phoneNumber.contains(query)
            }.toMutableList()
        }

        // Preserve selection state
        filteredContacts.forEach { contact ->
            val updatedContact = contact.copy(isSelected = selectedContacts.contains(contact.id))
            val index = filteredContacts.indexOf(contact)
            if (index != -1) {
                filteredContacts[index] = updatedContact
            }
        }

        adapter.submitList(filteredContacts.toList())
    }

    private fun handleContactSelection(contact: PhoneContact, isSelected: Boolean) {
        if (isSelected) {
            selectedContacts.add(contact.id)
        } else {
            selectedContacts.remove(contact.id)
        }

        updateSelectedCount()
        updateButtonState()

        // Update the contact in the list
        val index = filteredContacts.indexOfFirst { it.id == contact.id }
        if (index != -1) {
            filteredContacts[index] = contact.copy(isSelected = isSelected)
            adapter.notifyItemChanged(index)
        }
    }

    private fun updateSelectedCount() {
        val count = selectedContacts.size
        binding.tvSelectedCount.text = if (count > 0) {
            "Selected: $count"
        } else {
            "No contacts selected"
        }
    }

    private fun updateButtonState() {
        binding.btnAddSelected.isEnabled = selectedContacts.isNotEmpty()
        binding.btnAddSelected.alpha = if (selectedContacts.isNotEmpty()) 1.0f else 0.5f
    }

    private fun addSelectedContacts() {
        if (selectedContacts.isEmpty()) {
            Snackbar.make(binding.root, "No contacts selected", Snackbar.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val contactsToAdd = mutableListOf<EmergencyContact>()

                filteredContacts.filter { selectedContacts.contains(it.id) }.forEach { phoneContact ->
                    val emergencyContact = EmergencyContact(
                        name = phoneContact.name,
                        phoneNumber = phoneContact.phoneNumber,
                        type = com.example.firstaidapp.data.models.ContactType.PERSONAL,
                        relationship = "Imported from phone",
                        notes = "Added from phone contacts"
                    )
                    contactsToAdd.add(emergencyContact)
                }

                // Add contacts to database
                contactsToAdd.forEach { contact ->
                    try {
                        viewModel.addContact(contact)
                    } catch (e: Exception) {
                        // Ignore duplicates due to unique index (idempotent import)
                    }
                }

                withContext(Dispatchers.Main) {
                    setResult(RESULT_OK)
                    finish()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.root, "Error adding contacts: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            binding.root,
            "Contacts permission is required to import phone contacts",
            Snackbar.LENGTH_LONG
        ).setAction("Settings") {
            openAppSettings()
        }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
