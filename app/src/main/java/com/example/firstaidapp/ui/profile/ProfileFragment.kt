package com.example.firstaidapp.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.firstaidapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * ProfileFragment - User profile and medical information
 */
class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    // Profile UI elements
    private lateinit var tvProfileInitials: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileBio: TextView
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var switchDarkMode: SwitchMaterial

    // Medical Info UI elements
    private lateinit var cardMedicalInfo: CardView
    private lateinit var tvBloodType: TextView
    private lateinit var tvAllergies: TextView
    private lateinit var tvMedications: TextView
    private lateinit var tvMedicalConditions: TextView
    private lateinit var tvEmergencyNotes: TextView
    private lateinit var tvDoctorInfo: TextView
    private lateinit var btnEditMedicalInfo: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        // Initialize UI elements
        initViews(view)

        // Observe data
        observeData()

        // Set up listeners
        setupListeners()
    }

    private fun initViews(view: View) {
        // Profile elements
        tvProfileInitials = view.findViewById(R.id.tv_profile_initials)
        tvProfileName = view.findViewById(R.id.tv_profile_name)
        tvProfileBio = view.findViewById(R.id.tv_profile_bio)
        btnEditProfile = view.findViewById(R.id.btn_edit_profile)
        switchDarkMode = view.findViewById(R.id.switch_dark_mode)

        // Medical info elements
        cardMedicalInfo = view.findViewById(R.id.card_medical_info)
        tvBloodType = view.findViewById(R.id.tv_blood_type)
        tvAllergies = view.findViewById(R.id.tv_allergies)
        tvMedications = view.findViewById(R.id.tv_medications)
        tvMedicalConditions = view.findViewById(R.id.tv_medical_conditions)
        tvEmergencyNotes = view.findViewById(R.id.tv_emergency_notes)
        tvDoctorInfo = view.findViewById(R.id.tv_doctor_info)
        btnEditMedicalInfo = view.findViewById(R.id.btn_edit_medical_info)

        // Set dark mode switch state
        switchDarkMode.isChecked = viewModel.isDarkModeEnabled()
    }

    private fun observeData() {
        // Observe user profile
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            tvProfileInitials.text = viewModel.getUserInitials()
            tvProfileName.text = if (profile.name.isEmpty()) "Set your name" else profile.name
            tvProfileBio.text = if (profile.bio.isEmpty()) "Add a bio" else profile.bio
        }

        // Observe medical info
        viewModel.medicalInfo.observe(viewLifecycleOwner) { medical ->
            tvBloodType.text = if (medical.bloodType.isEmpty()) "Not set" else medical.bloodType
            tvAllergies.text = if (medical.allergies.isEmpty()) "None listed" else medical.allergies
            tvMedications.text = if (medical.medications.isEmpty()) "None listed" else medical.medications
            tvMedicalConditions.text = if (medical.medicalConditions.isEmpty()) "None listed" else medical.medicalConditions
            tvEmergencyNotes.text = if (medical.emergencyNotes.isEmpty()) "None" else medical.emergencyNotes

            val doctorInfo = if (medical.doctorName.isEmpty()) {
                "No doctor information"
            } else {
                "${medical.doctorName}${if (medical.doctorContact.isNotEmpty()) " - ${medical.doctorContact}" else ""}"
            }
            tvDoctorInfo.text = doctorInfo
        }
    }

    private fun setupListeners() {
        // Edit profile button
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        // Dark mode switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleDarkMode()
            Toast.makeText(requireContext(),
                if (isChecked) "Dark mode enabled (restart app to apply)" else "Light mode enabled (restart app to apply)",
                Toast.LENGTH_SHORT).show()
        }

        // Edit medical info button
        btnEditMedicalInfo.setOnClickListener {
            showEditMedicalInfoDialog()
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_name)
        val etBio = dialogView.findViewById<EditText>(R.id.et_bio)

        // Pre-fill with current values
        val currentProfile = viewModel.userProfile.value
        etName.setText(currentProfile?.name ?: "")
        etBio.setText(currentProfile?.bio ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString().trim()
                val bio = etBio.text.toString().trim()
                viewModel.saveUserProfile(name, bio)
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditMedicalInfoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_medical_info, null)

        val etBloodType = dialogView.findViewById<EditText>(R.id.et_blood_type)
        val etAllergies = dialogView.findViewById<EditText>(R.id.et_allergies)
        val etMedications = dialogView.findViewById<EditText>(R.id.et_medications)
        val etMedicalConditions = dialogView.findViewById<EditText>(R.id.et_medical_conditions)
        val etEmergencyNotes = dialogView.findViewById<EditText>(R.id.et_emergency_notes)
        val etDoctorName = dialogView.findViewById<EditText>(R.id.et_doctor_name)
        val etDoctorContact = dialogView.findViewById<EditText>(R.id.et_doctor_contact)

        // Pre-fill with current values
        val currentMedical = viewModel.medicalInfo.value
        etBloodType.setText(currentMedical?.bloodType ?: "")
        etAllergies.setText(currentMedical?.allergies ?: "")
        etMedications.setText(currentMedical?.medications ?: "")
        etMedicalConditions.setText(currentMedical?.medicalConditions ?: "")
        etEmergencyNotes.setText(currentMedical?.emergencyNotes ?: "")
        etDoctorName.setText(currentMedical?.doctorName ?: "")
        etDoctorContact.setText(currentMedical?.doctorContact ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Medical Information")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                viewModel.saveMedicalInfo(
                    bloodType = etBloodType.text.toString().trim(),
                    allergies = etAllergies.text.toString().trim(),
                    medications = etMedications.text.toString().trim(),
                    medicalConditions = etMedicalConditions.text.toString().trim(),
                    emergencyNotes = etEmergencyNotes.text.toString().trim(),
                    doctorName = etDoctorName.text.toString().trim(),
                    doctorContact = etDoctorContact.text.toString().trim()
                )
                Toast.makeText(requireContext(), "Medical information updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

