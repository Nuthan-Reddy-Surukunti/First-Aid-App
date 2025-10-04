package com.example.firstaidapp.ui.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.databinding.FragmentContactsBinding
import com.google.android.material.snackbar.Snackbar

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
        binding.fabAddContact.setOnClickListener {
            // TODO: Show dialog to add new contact
            Snackbar.make(binding.root, "Add contact feature coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission granted - make direct call
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            }
            else -> {
                // No permission - use dialer (safe approach)
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)

                // Optionally ask for permission for future direct calls
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
