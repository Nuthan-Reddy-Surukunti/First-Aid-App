package com.example.firstaidapp.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.databinding.FragmentContactsBinding
import com.example.firstaidapp.utils.LocationHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ContactsViewModel
    private lateinit var contactsAdapter: ContactsAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, can make direct call
            Snackbar.make(binding.root, "Permission granted! Tap call button again to make direct call", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(binding.root, "Using dialer instead", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permission granted, get location
            detectLocationAndSetState()
        } else {
            Snackbar.make(binding.root, "Location permission denied. Please select your state manually.", Snackbar.LENGTH_LONG).show()
            showManualStateSelection()
        }
    }

    private val requestContactsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        viewModel.setContactsPermissionGranted(isGranted)
        if (isGranted) {
            Snackbar.make(binding.root, "Permission granted! Loading contacts...", Snackbar.LENGTH_SHORT).show()
            showPhoneContactPickerDialog()
        } else {
            Snackbar.make(binding.root, "Permission denied. You can enable it in Settings to import contacts.", Snackbar.LENGTH_LONG).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearchFunctionality()

        // Only show state dialog if no state has been selected before
        if (!viewModel.isStateSelected()) {
            showStateDialogIfNeeded()
        }
    }

    private fun setupViewModel() {
        val factory = ContactsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ContactsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter(
            onCallClick = { contact -> makePhoneCall(contact.phoneNumber) },
            onContactClick = { contact -> showContactDetailsDialog(contact) },
            onEditClick = { contact -> showEditContactDialog(contact) },
            onDeleteClick = { contact -> showDeleteConfirmationDialog(contact) }
        )

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun setupObservers() {
        // Observe all contacts (filtered by state)
        viewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            // Only update if no search is active
            if (binding.etSearchContacts.text.isNullOrEmpty()) {
                contactsAdapter.submitList(contacts)
            }
        }

        // Observe filtered contacts (for search results)
        viewModel.filteredContacts.observe(viewLifecycleOwner) { filteredContacts ->
            // Only update if search is active and we have filtered results
            if (!binding.etSearchContacts.text.isNullOrEmpty() && filteredContacts.isNotEmpty()) {
                contactsAdapter.submitList(filteredContacts)
            }
        }

        // Observe selected state to update button text
        viewModel.selectedState.observe(viewLifecycleOwner) { state ->
            updateStateButtonText(state)
        }
    }

    private fun setupClickListeners() {
        binding.btnSettings.setOnClickListener {
            // Use basic navigation instead of SafeArgs for compatibility
            findNavController().navigate(R.id.navigation_settings)
        }

        binding.fabAddContact.setOnClickListener {
            showAddContactDialog()
        }
        
        binding.btnSelectState.setOnClickListener {
            showStateSelectionDialog()
        }
    }

    private fun setupSearchFunctionality() {
        // Add null check to prevent crashes
        if (_binding == null) return

        binding.etSearchContacts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Add null check
                if (_binding == null) return

                val query = s.toString()

                // Removed all animations - immediate search instead
                if (query.isNotEmpty()) {
                    try {
                        viewModel.searchContacts(query)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        viewModel.clearSearch()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Removed focus animations - basic functionality only
        binding.etSearchContacts.setOnFocusChangeListener { view, hasFocus ->
            if (_binding == null) return@setOnFocusChangeListener
            // No animations - just basic elevation change
            if (hasFocus) {
                view.elevation = 8f
            } else {
                view.elevation = 2f
            }
        }
    }

    private fun showAddContactDialog() {
        // Use the new method with empty prefill data
        showAddContactDialogWithData("", "")
    }

    private fun showAddContactDialogOld() {
        // Add null check to prevent crashes
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Get dialog views
            val etContactName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etContactName)
            val etPhoneNumber = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhoneNumber)
            val etRelationship = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etRelationship)
            val spinnerContactType = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerContactType)
            val etNotes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNotes)
            val btnImportFromPhone = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnImportFromPhone)
            val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)

            // Null checks for all views
            if (etContactName == null || etPhoneNumber == null || spinnerContactType == null ||
                btnImportFromPhone == null || btnCancel == null || btnSave == null) {
                return
            }

            // Setup contact type dropdown
            setupContactTypeDropdown(spinnerContactType)

            // Setup click listeners with null checks
            btnImportFromPhone.setOnClickListener {
                dialog.dismiss()
                if (isAdded && _binding != null) {
                    openPhoneContactsSelection()
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                if (!isAdded || _binding == null) {
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val name = etContactName.text?.toString()?.trim() ?: ""
                val phone = etPhoneNumber.text?.toString()?.trim() ?: ""
                val relationship = etRelationship.text?.toString()?.trim() ?: ""
                val typeString = spinnerContactType.text?.toString() ?: "Personal"
                val notes = etNotes.text?.toString()?.trim() ?: ""

                if (validateContactInput(name, phone)) {
                    val contactType = when (typeString) {
                        "Emergency Service" -> ContactType.EMERGENCY_SERVICE
                        "Poison Control" -> ContactType.POISON_CONTROL
                        "Hospital" -> ContactType.HOSPITAL
                        "Police" -> ContactType.POLICE
                        "Fire Department" -> ContactType.FIRE_DEPARTMENT
                        "Family" -> ContactType.FAMILY
                        "Doctor" -> ContactType.DOCTOR
                        "Veterinarian" -> ContactType.VETERINARIAN
                        "Other" -> ContactType.OTHER
                        else -> ContactType.PERSONAL
                    }

                    val contact = EmergencyContact(
                        name = name,
                        phoneNumber = phone,
                        relationship = relationship,
                        type = contactType,
                        notes = notes,
                        // Assign currently selected state for manual contacts
                        state = viewModel.selectedState.value ?: "National"
                    )

                    try {
                        viewModel.addContact(contact)

                        // Removed animation - show immediate success message
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Contact added successfully!", Snackbar.LENGTH_SHORT).show()
                        }

                        dialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Error adding contact: ${e.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error opening add contact dialog", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupContactTypeDropdown(spinner: AutoCompleteTextView) {
        val contactTypes = arrayOf(
            "Personal",
            "Family",
            "Emergency Service",
            "Hospital",
            "Police",
            "Fire Department",
            "Poison Control",
            "Doctor",
            "Veterinarian",
            "Other"
        )

        val adapter = android.widget.ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            contactTypes
        )

        spinner.setAdapter(adapter)
        spinner.setText("Personal", false)
    }

    private fun openPhoneContactsSelection() {
        // Add safety checks to prevent crashes
        if (_binding == null || !isAdded) return

        try {
            // Check if permission is already granted (from SharedPreferences)
            if (viewModel.isContactsPermissionGranted()) {
                // Check runtime permission as well
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission granted, show contact picker
                    showPhoneContactPickerDialog()
                } else {
                    // Permission was revoked in system settings
                    viewModel.setContactsPermissionGranted(false)
                    requestContactsPermission()
                }
            } else if (viewModel.hasAskedContactsPermission()) {
                // Previously denied, show message to enable in settings
                showContactsPermissionDeniedDialog()
            } else {
                // First time asking
                requestContactsPermission()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error accessing phone contacts", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestContactsPermission() {
        if (_binding == null || !isAdded) return

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Already granted
                viewModel.setContactsPermissionGranted(true)
                showPhoneContactPickerDialog()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                // Show rationale
                AlertDialog.Builder(requireContext())
                    .setTitle("Contacts Permission Needed")
                    .setMessage(getString(R.string.contacts_permission_rationale))
                    .setPositiveButton("Grant Permission") { _, _ ->
                        requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                        viewModel.setContactsPermissionAsked(true)
                    }
                    .show()
            }
            else -> {
                // Request permission
                requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun showContactsPermissionDeniedDialog() {
        if (_binding == null || !isAdded) return

        AlertDialog.Builder(requireContext())
            .setTitle("Contacts Permission Required")
            .setMessage(getString(R.string.contacts_permission_permanently_denied))
            .setPositiveButton("Open Settings") { _, _ ->
                try {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = "package:${requireContext().packageName}".toUri()
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showPhoneContactPickerDialog() {
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_phone_contacts_picker, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            val rvPhoneContacts = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPhoneContacts)
            val etSearch = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etSearchPhoneContacts)
            val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelPicker)
            val btnAddSelected = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddSelected)
            val tvSelectionInfo = dialogView.findViewById<android.widget.TextView>(R.id.tvSelectionInfo)
            val progressBar = dialogView.findViewById<android.widget.ProgressBar>(R.id.progressBar)
            val emptyState = dialogView.findViewById<ViewGroup>(R.id.layoutEmptyState)

            if (rvPhoneContacts == null || etSearch == null || btnCancel == null || btnAddSelected == null ||
                tvSelectionInfo == null || progressBar == null || emptyState == null) {
                Snackbar.make(binding.root, "Error loading contact picker layout", Snackbar.LENGTH_SHORT).show()
                return
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            rvPhoneContacts.visibility = View.GONE

            val phoneContactsAdapter = PhoneContactsAdapter { selectedContacts ->
                // Update selection info text
                val count = selectedContacts.size
                tvSelectionInfo.text = when (count) {
                    0 -> "No contacts selected"
                    1 -> "1 contact selected"
                    else -> "$count contacts selected"
                }

                // Enable/disable add button based on selection
                btnAddSelected.isEnabled = count > 0
            }

            rvPhoneContacts.layoutManager = LinearLayoutManager(requireContext())
            rvPhoneContacts.adapter = phoneContactsAdapter

            // Load contacts in background
            lifecycleScope.launch {
                try {
                    // Check permission again before accessing contacts
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.READ_CONTACTS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        progressBar.visibility = View.GONE
                        dialog.dismiss()
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Contacts permission not granted", Snackbar.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val contacts = com.example.firstaidapp.utils.ContactsHelper.getPhoneContacts(requireContext())

                    if (!isAdded || _binding == null) {
                        dialog.dismiss()
                        return@launch
                    }

                    progressBar.visibility = View.GONE

                    if (contacts.isEmpty()) {
                        emptyState.visibility = View.VISIBLE
                        rvPhoneContacts.visibility = View.GONE
                    } else {
                        rvPhoneContacts.visibility = View.VISIBLE
                        emptyState.visibility = View.GONE
                        phoneContactsAdapter.submitList(contacts)

                        // Setup search
                        etSearch.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                val query = s.toString()
                                val filtered = com.example.firstaidapp.utils.ContactsHelper.searchContacts(contacts, query)
                                phoneContactsAdapter.submitList(filtered)
                            }
                            override fun afterTextChanged(s: Editable?) {}
                        })
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                    progressBar.visibility = View.GONE
                    dialog.dismiss()
                    viewModel.setContactsPermissionGranted(false)
                    if (_binding != null) {
                        Snackbar.make(binding.root, "Contacts permission was revoked. Please grant it again.", Snackbar.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    progressBar.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    if (_binding != null) {
                        Snackbar.make(binding.root, "Error loading contacts: ${e.message}", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnAddSelected.setOnClickListener {
                val selectedContacts = phoneContactsAdapter.getSelectedContacts()
                if (selectedContacts.isNotEmpty()) {
                    dialog.dismiss()
                    addMultipleContacts(selectedContacts)
                }
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error opening contact picker", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun prefillContactData(contact: com.example.firstaidapp.data.models.PhoneContact) {
        if (_binding == null || !isAdded) return

        // Show the add contact dialog with pre-filled data
        showAddContactDialogWithData(contact.name, contact.phoneNumber)
    }

    private fun addMultipleContacts(contacts: List<com.example.firstaidapp.data.models.PhoneContact>) {
        if (_binding == null || !isAdded) return

        lifecycleScope.launch {
            try {
                var successCount = 0
                var failCount = 0

                contacts.forEach { phoneContact ->
                    try {
                        val contact = EmergencyContact(
                            name = phoneContact.name,
                            phoneNumber = phoneContact.phoneNumber,
                            relationship = "",
                            type = ContactType.PERSONAL,
                            notes = "Imported from phone contacts",
                            state = viewModel.selectedState.value ?: "National"
                        )
                        viewModel.addContact(contact)
                        successCount++
                    } catch (e: Exception) {
                        e.printStackTrace()
                        failCount++
                    }
                }

                if (_binding != null) {
                    val message = when {
                        failCount == 0 -> "$successCount contact${if (successCount == 1) "" else "s"} added successfully!"
                        successCount == 0 -> "Failed to add contacts"
                        else -> "$successCount contact${if (successCount == 1) "" else "s"} added, $failCount failed"
                    }
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_binding != null) {
                    Snackbar.make(binding.root, "Error adding contacts: ${e.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showAddContactDialogWithData(prefillName: String = "", prefillPhone: String = "") {
        // Add null check to prevent crashes
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Get dialog views
            val etContactName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etContactName)
            val etPhoneNumber = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etPhoneNumber)
            val etRelationship = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etRelationship)
            val spinnerContactType = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerContactType)
            val etNotes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etNotes)
            val btnImportFromPhone = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnImportFromPhone)
            val btnCancel = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            val btnSave = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSave)

            // Null checks for all views
            if (etContactName == null || etPhoneNumber == null || spinnerContactType == null ||
                btnImportFromPhone == null || btnCancel == null || btnSave == null) {
                return
            }

            // Prefill data if provided
            etContactName.setText(prefillName)
            etPhoneNumber.setText(prefillPhone)

            // Setup contact type dropdown
            setupContactTypeDropdown(spinnerContactType)

            // Hide import button if already importing
            if (prefillName.isNotEmpty()) {
                btnImportFromPhone.visibility = View.GONE
            }

            // Setup click listeners with null checks
            btnImportFromPhone.setOnClickListener {
                dialog.dismiss()
                if (isAdded && _binding != null) {
                    openPhoneContactsSelection()
                }
            }

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnSave.setOnClickListener {
                if (!isAdded || _binding == null) {
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val name = etContactName.text?.toString()?.trim() ?: ""
                val phone = etPhoneNumber.text?.toString()?.trim() ?: ""
                val relationship = etRelationship.text?.toString()?.trim() ?: ""
                val typeString = spinnerContactType.text?.toString() ?: "Personal"
                val notes = etNotes.text?.toString()?.trim() ?: ""

                if (validateContactInput(name, phone)) {
                    val contactType = when (typeString) {
                        "Emergency Service" -> ContactType.EMERGENCY_SERVICE
                        "Poison Control" -> ContactType.POISON_CONTROL
                        "Hospital" -> ContactType.HOSPITAL
                        "Police" -> ContactType.POLICE
                        "Fire Department" -> ContactType.FIRE_DEPARTMENT
                        "Family" -> ContactType.FAMILY
                        "Doctor" -> ContactType.DOCTOR
                        "Veterinarian" -> ContactType.VETERINARIAN
                        "Other" -> ContactType.OTHER
                        else -> ContactType.PERSONAL
                    }

                    val contact = EmergencyContact(
                        name = name,
                        phoneNumber = phone,
                        relationship = relationship,
                        type = contactType,
                        notes = notes,
                        state = viewModel.selectedState.value ?: "National"
                    )

                    try {
                        viewModel.addContact(contact)

                        if (_binding != null) {
                            Snackbar.make(binding.root, "Contact added successfully!", Snackbar.LENGTH_SHORT).show()
                        }

                        dialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Error adding contact: ${e.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error opening add contact dialog", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        // Add comprehensive null checks and error handling
        if (_binding == null || !isAdded) return

        try {
            // Enhanced call animation with haptic feedback
            binding.root.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

            // Check for call permission
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Direct call
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = "tel:$phoneNumber".toUri()
                    }
                    if (callIntent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(callIntent)
                    } else {
                        // Fallback to dialer if direct call not available
                        makeDialerCall(phoneNumber)
                    }
                }
                else -> {
                    makeDialerCall(phoneNumber)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to dialer on any error
            makeDialerCall(phoneNumber)
        }
    }

    private fun makeDialerCall(phoneNumber: String) {
        try {
            // Use dialer as safe fallback
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:$phoneNumber".toUri()
            }
            if (dialIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(dialIntent)
            } else if (_binding != null) {
                Snackbar.make(binding.root, "No dialer app available", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error making call", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateContactInput(name: String, phone: String): Boolean {
        // Add null check to prevent crashes
        if (_binding == null) return false

        if (name.isEmpty()) {
            Snackbar.make(binding.root, "Please enter a contact name", Snackbar.LENGTH_SHORT).show()
            return false
        }

        if (phone.isEmpty()) {
            Snackbar.make(binding.root, "Please enter a phone number", Snackbar.LENGTH_SHORT).show()
            return false
        }

        // Basic phone number validation
        val phoneRegex = "^[+]?[0-9()]{7,15}$".toRegex()
        if (!phone.matches(phoneRegex)) {
            Snackbar.make(binding.root, "Please enter a valid phone number", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showContactDetailsDialog(contact: EmergencyContact) {
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_contact_details, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Get all views
            val tvDetailName = dialogView.findViewById<android.widget.TextView>(R.id.tvDetailName)
            val tvDetailPhone = dialogView.findViewById<android.widget.TextView>(R.id.tvDetailPhone)
            val tvDetailRelationship = dialogView.findViewById<android.widget.TextView>(R.id.tvDetailRelationship)
            val tvDetailState = dialogView.findViewById<android.widget.TextView>(R.id.tvDetailState)
            val tvDetailNotes = dialogView.findViewById<android.widget.TextView>(R.id.tvDetailNotes)
            val tvContactTypeBadge = dialogView.findViewById<android.widget.TextView>(R.id.tvContactTypeBadge)
            val layoutRelationship = dialogView.findViewById<ViewGroup>(R.id.layoutRelationship)
            val layoutNotes = dialogView.findViewById<ViewGroup>(R.id.layoutNotes)
            val layoutDefaultIndicator = dialogView.findViewById<ViewGroup>(R.id.layoutDefaultIndicator)
            val btnCallFromDetails = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCallFromDetails)
            val btnEditFromDetails = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEditFromDetails)
            val btnDeleteFromDetails = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDeleteFromDetails)
            val btnCloseDetails = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCloseDetails)

            // Set contact data
            tvDetailName?.text = contact.name
            tvDetailPhone?.text = contact.phoneNumber
            tvDetailState?.text = contact.state
            tvContactTypeBadge?.text = contact.type.name.replace("_", " ")

            // Show/hide relationship
            if (!contact.relationship.isNullOrEmpty()) {
                layoutRelationship?.visibility = View.VISIBLE
                tvDetailRelationship?.text = contact.relationship
            } else {
                layoutRelationship?.visibility = View.GONE
            }

            // Show/hide notes
            if (!contact.notes.isNullOrEmpty()) {
                layoutNotes?.visibility = View.VISIBLE
                tvDetailNotes?.text = contact.notes
            } else {
                layoutNotes?.visibility = View.GONE
            }

            // Show/hide default indicator
            if (contact.isDefault) {
                layoutDefaultIndicator?.visibility = View.VISIBLE
            } else {
                layoutDefaultIndicator?.visibility = View.GONE
            }

            // Setup button clicks
            btnCallFromDetails?.setOnClickListener {
                dialog.dismiss()
                makePhoneCall(contact.phoneNumber)
            }

            btnEditFromDetails?.setOnClickListener {
                dialog.dismiss()
                showEditContactDialog(contact)
            }

            btnDeleteFromDetails?.setOnClickListener {
                dialog.dismiss()
                showDeleteConfirmationDialog(contact)
            }

            btnCloseDetails?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error showing contact details", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditContactDialog(contact: EmergencyContact) {
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_edit_contact, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Get dialog views
            val etEditContactName = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditContactName)
            val etEditPhoneNumber = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditPhoneNumber)
            val etEditRelationship = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditRelationship)
            val spinnerEditContactType = dialogView.findViewById<AutoCompleteTextView>(R.id.spinnerEditContactType)
            val etEditNotes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEditNotes)
            val btnCancelEdit = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancelEdit)
            val btnSaveEdit = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnSaveEdit)

            // Null checks for all views
            if (etEditContactName == null || etEditPhoneNumber == null || spinnerEditContactType == null ||
                btnCancelEdit == null || btnSaveEdit == null) {
                return
            }

            // Pre-fill existing data
            etEditContactName.setText(contact.name)
            etEditPhoneNumber.setText(contact.phoneNumber)
            etEditRelationship?.setText(contact.relationship ?: "")
            etEditNotes?.setText(contact.notes ?: "")

            // Setup contact type dropdown
            setupContactTypeDropdown(spinnerEditContactType)

            // Set current contact type
            val typeString = when (contact.type) {
                ContactType.EMERGENCY_SERVICE -> "Emergency Service"
                ContactType.POISON_CONTROL -> "Poison Control"
                ContactType.HOSPITAL -> "Hospital"
                ContactType.POLICE -> "Police"
                ContactType.FIRE_DEPARTMENT -> "Fire Department"
                ContactType.FAMILY -> "Family"
                ContactType.DOCTOR -> "Doctor"
                ContactType.VETERINARIAN -> "Veterinarian"
                ContactType.OTHER -> "Other"
                else -> "Personal"
            }
            spinnerEditContactType.setText(typeString, false)

            // Setup click listeners
            btnCancelEdit.setOnClickListener {
                dialog.dismiss()
            }

            btnSaveEdit.setOnClickListener {
                if (!isAdded || _binding == null) {
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val name = etEditContactName.text?.toString()?.trim() ?: ""
                val phone = etEditPhoneNumber.text?.toString()?.trim() ?: ""
                val relationship = etEditRelationship?.text?.toString()?.trim() ?: ""
                val typeString = spinnerEditContactType.text?.toString() ?: "Personal"
                val notes = etEditNotes?.text?.toString()?.trim() ?: ""

                if (validateContactInput(name, phone)) {
                    val contactType = when (typeString) {
                        "Emergency Service" -> ContactType.EMERGENCY_SERVICE
                        "Poison Control" -> ContactType.POISON_CONTROL
                        "Hospital" -> ContactType.HOSPITAL
                        "Police" -> ContactType.POLICE
                        "Fire Department" -> ContactType.FIRE_DEPARTMENT
                        "Family" -> ContactType.FAMILY
                        "Doctor" -> ContactType.DOCTOR
                        "Veterinarian" -> ContactType.VETERINARIAN
                        "Other" -> ContactType.OTHER
                        else -> ContactType.PERSONAL
                    }

                    val updatedContact = contact.copy(
                        name = name,
                        phoneNumber = phone,
                        relationship = relationship,
                        type = contactType,
                        notes = notes
                    )

                    try {
                        viewModel.updateContact(updatedContact)

                        if (_binding != null) {
                            Snackbar.make(binding.root, "Contact updated successfully!", Snackbar.LENGTH_SHORT).show()
                        }

                        dialog.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Error updating contact: ${e.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error opening edit contact dialog", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog(contact: EmergencyContact) {
        if (_binding == null || !isAdded) return

        try {
            val message = if (contact.isDefault) {
                "Are you sure you want to hide '${contact.name}'? This is a system contact and will be hidden from your list."
            } else {
                "Are you sure you want to delete '${contact.name}'? This action cannot be undone."
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Delete Contact")
                .setMessage(message)
                .setPositiveButton("Delete") { _, _ ->
                    try {
                        viewModel.deleteContact(contact)

                        if (_binding != null) {
                            val snackbar = Snackbar.make(
                                binding.root,
                                "Contact deleted successfully",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Error deleting contact: ${e.message}", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error showing delete dialog", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateStateButtonText(state: String?) {
        if (_binding == null) return

        if (state.isNullOrEmpty()) {
            binding.btnSelectState.contentDescription = "Select State"
        } else {
            binding.btnSelectState.contentDescription = "Current: $state (tap to change)"
        }
    }

    private fun showStateSelectionDialog() {
        if (_binding == null || !isAdded) return

        try {
            val currentState = viewModel.selectedState.value
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_select_state, null)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            val btnUseLocation = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnUseLocation)
            val btnManualSelection = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnManualSelection)
            val btnShowAll = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnShowAll)

            btnUseLocation?.setOnClickListener {
                dialog.dismiss()
                requestLocationAndDetectState()
            }

            btnManualSelection?.setOnClickListener {
                dialog.dismiss()
                showManualStateSelection()
            }

            btnShowAll?.setOnClickListener {
                dialog.dismiss()
                viewModel.clearSelectedState()
                Snackbar.make(binding.root, "State cleared - showing all contacts", Snackbar.LENGTH_SHORT).show()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error showing state selection", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showStateDialogIfNeeded() {
        AlertDialog.Builder(requireContext())
            .setTitle("Welcome! Select Your State")
            .setMessage("To show relevant emergency contacts for your area, please select your state.")
            .setPositiveButton("Select State") { _, _ ->
                showStateSelectionDialog()
             }
             .setNegativeButton("Use GPS") { _, _ ->
                 requestLocationAndDetectState()
             }
             .setCancelable(false)
             .show()
    }

    private fun showManualStateSelection() {
        if (_binding == null || !isAdded) return

        try {
            val states = com.example.firstaidapp.data.repository.EmergencyContactsData.getStatesList()
            val stateArray = states.toTypedArray()

            AlertDialog.Builder(requireContext())
                .setTitle("Select Your State")
                .setItems(stateArray) { dialog, which ->
                    val selectedState = stateArray[which]
                    viewModel.setSelectedState(selectedState)
                    Snackbar.make(binding.root, "Showing contacts for $selectedState", Snackbar.LENGTH_LONG).show()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error showing state list", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocationAndDetectState() {
        val locationHelper = LocationHelper(requireContext())

        if (locationHelper.hasLocationPermission()) {
            detectLocationAndSetState()
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun detectLocationAndSetState() {
        if (_binding == null || !isAdded) return

        val locationHelper = LocationHelper(requireContext())

        lifecycleScope.launch {
            try {
                if (_binding == null) return@launch

                Snackbar.make(binding.root, "Detecting your location...", Snackbar.LENGTH_SHORT).show()

                if (!locationHelper.hasLocationPermission()) {
                    if (_binding != null) {
                        Snackbar.make(binding.root, "Location permission not granted", Snackbar.LENGTH_SHORT).show()
                        showManualStateSelection()
                    }
                    return@launch
                }

                val state = locationHelper.getCurrentState()

                if (_binding == null) return@launch

                if (state != null) {
                    viewModel.setSelectedState(state)
                    Snackbar.make(binding.root, "Location detected: $state", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, "Could not detect your location. Please select your state manually.", Snackbar.LENGTH_LONG).show()
                    showManualStateSelection()
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                if (_binding != null) {
                    Snackbar.make(binding.root, "Location permission was revoked. Please grant it again.", Snackbar.LENGTH_LONG).show()
                    showManualStateSelection()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (_binding != null) {
                    Snackbar.make(binding.root, "Error detecting location. Please select manually.", Snackbar.LENGTH_LONG).show()
                    showManualStateSelection()
                }
            }
        }
    }
}
