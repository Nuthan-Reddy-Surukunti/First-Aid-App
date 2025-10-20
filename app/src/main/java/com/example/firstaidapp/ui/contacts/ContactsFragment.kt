package com.example.firstaidapp.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.R
import kotlinx.coroutines.launch
import com.example.firstaidapp.data.models.ContactType
import com.example.firstaidapp.data.models.EmergencyContact
import com.example.firstaidapp.databinding.FragmentContactsBinding
import com.google.android.material.snackbar.Snackbar
import androidx.core.net.toUri

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
    }

    private fun setupViewModel() {
        val factory = ContactsViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[ContactsViewModel::class.java]
    }

    private fun setupRecyclerView() {
        contactsAdapter = ContactsAdapter { contact ->
            makePhoneCall(contact.phoneNumber)
        }

        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.allContacts.observe(viewLifecycleOwner) { contacts ->
            contactsAdapter.submitList(contacts)
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
        // Add null check to prevent crashes
        if (_binding == null || !isAdded) return

        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_contact, null)

            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
                        notes = notes
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
            // PhoneContactsActivity was removed - show message that feature is not available
            if (_binding != null) {
                Snackbar.make(binding.root, "Phone contacts import not available in this version", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error accessing phone contacts", Snackbar.LENGTH_SHORT).show()
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
        val phoneRegex = "^[+]?[0-9\\s\\-\\(\\)]{7,15}$".toRegex()
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

    private fun showStateSelectionDialog() {
        if (_binding == null || !isAdded) return
        
        try {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_select_state, null)
            
            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()
            
            val btnUseLocation = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnUseLocation)
            val btnManualSelection = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnManualSelection)
            val btnShowAll = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnShowAll)
            
            btnUseLocation.setOnClickListener {
                dialog.dismiss()
                requestLocationAndDetectState()
            }
            
            btnManualSelection.setOnClickListener {
                dialog.dismiss()
                showManualStateSelection()
            }
            
            btnShowAll.setOnClickListener {
                dialog.dismiss()
                viewModel.setSelectedState(null)
                Snackbar.make(binding.root, "Showing all contacts", Snackbar.LENGTH_SHORT).show()
            }
            
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            if (_binding != null) {
                Snackbar.make(binding.root, "Error showing state selection", Snackbar.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun requestLocationAndDetectState() {
        val locationHelper = com.example.firstaidapp.utils.LocationHelper(requireContext())
        
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
        
        val locationHelper = com.example.firstaidapp.utils.LocationHelper(requireContext())
        
        androidx.lifecycle.lifecycleScope.launch {
            try {
                Snackbar.make(binding.root, "Detecting your location...", Snackbar.LENGTH_SHORT).show()
                
                val state = locationHelper.getCurrentState()
                
                if (state != null) {
                    viewModel.setSelectedState(state)
                    Snackbar.make(binding.root, "Showing contacts for $state", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(binding.root, "Could not detect state. Please select manually.", Snackbar.LENGTH_LONG).show()
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
    
    private fun showManualStateSelection() {
        if (_binding == null || !isAdded) return
        
        try {
            val states = com.example.firstaidapp.data.repository.EmergencyContactsData.getStatesList()
            val stateArray = states.toTypedArray()
            
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
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

    override fun onPause() {
        super.onPause()
    }
}
