package com.example.firstaidapp.ui.voice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.firstaidapp.databinding.FragmentVoiceAssistantBinding
import com.example.firstaidapp.voice.VoiceAssistantState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Fragment for Voice Assistant Interface
 */
class VoiceAssistantFragment : Fragment() {

    private var _binding: FragmentVoiceAssistantBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VoiceAssistantViewModel by viewModels()

    // Permission launcher for runtime permission requests
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Log.d("VoiceAssistantFragment", "All permissions granted, initializing voice assistant")
            Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
            viewModel.initialize()
        } else {
            val deniedPermissions = permissions.filter { !it.value }.keys
            Log.w("VoiceAssistantFragment", "Permissions denied: $deniedPermissions")
            showPermissionDeniedDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceAssistantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
        checkAndRequestPermissions()
    }

    private fun setupUI() {
        // Main microphone button
        binding.microphoneButton.setOnClickListener {
            handleMicrophoneClick()
        }

        // Emergency quick actions
        binding.btnEmergencyCpr.setOnClickListener {
            viewModel.startCPRGuidance()
        }

        binding.btnEmergencyChoking.setOnClickListener {
            viewModel.startChokingGuidance()
        }

        binding.btnEmergencyBleeding.setOnClickListener {
            viewModel.startBleedingGuidance()
        }

        binding.btnEmergencyBurns.setOnClickListener {
            viewModel.startBurnsGuidance()
        }

        // Stop button
        binding.btnStop.setOnClickListener {
            viewModel.stopSpeaking()
            viewModel.stopListening()
        }

        // Clear conversation button
        binding.btnClear.setOnClickListener {
            viewModel.clearConversation()
            binding.tvResponse.text = ""
            binding.tvRecognizedText.text = ""
            Toast.makeText(requireContext(), "Conversation cleared", Toast.LENGTH_SHORT).show()
        }

        // Exit emergency mode button
        binding.btnExitEmergency.setOnClickListener {
            viewModel.exitEmergencyMode()
        }
    }

    private fun observeViewModel() {
        // Assistant state
        viewModel.assistantState.observe(viewLifecycleOwner) { state ->
            updateUIForState(state)
        }

        // Recognized text
        viewModel.recognizedText.observe(viewLifecycleOwner) { text ->
            binding.tvRecognizedText.text = text
        }

        // AI Response
        viewModel.currentResponse.observe(viewLifecycleOwner) { response ->
            response?.let {
                binding.tvResponse.text = it.text ?: "No response available"

                // Show urgency indicator if present
                val urgency = it.metadata?.get("urgency") as? String
                when (urgency) {
                    "HIGH" -> binding.urgencyIndicator.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                    )
                    "MEDIUM" -> binding.urgencyIndicator.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark)
                    )
                    else -> binding.urgencyIndicator.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
                    )
                }
            } ?: run {
                binding.tvResponse.text = "AI features unavailable - using offline mode"
                binding.urgencyIndicator.setBackgroundColor(
                    ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
                )
            }
        }

        // Error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
        }

        // Initialization status
        viewModel.isInitialized.observe(viewLifecycleOwner) { initialized ->
            if (initialized) {
                binding.microphoneButton.isEnabled = true
                binding.statusText.text = "Voice Assistant Ready"
            } else {
                binding.microphoneButton.isEnabled = false
                binding.statusText.text = "Initializing..."
            }
        }

        // Emergency mode
        viewModel.showEmergencyMode.observe(viewLifecycleOwner) { showEmergency ->
            if (showEmergency) {
                binding.emergencyOverlay.visibility = View.VISIBLE
                binding.normalModeLayout.visibility = View.GONE
            } else {
                binding.emergencyOverlay.visibility = View.GONE
                binding.normalModeLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun updateUIForState(state: VoiceAssistantState) {
        when (state) {
            is VoiceAssistantState.Idle -> {
                binding.microphoneButton.isEnabled = true
                binding.statusText.text = "Tap to speak"
                binding.listeningAnimation.visibility = View.GONE
                binding.processingAnimation.visibility = View.GONE
                binding.speakingAnimation.visibility = View.GONE
            }
            is VoiceAssistantState.Listening -> {
                binding.statusText.text = "Listening..."
                binding.listeningAnimation.visibility = View.VISIBLE
                binding.processingAnimation.visibility = View.GONE
                binding.speakingAnimation.visibility = View.GONE
            }
            is VoiceAssistantState.Processing -> {
                binding.statusText.text = "Processing..."
                binding.listeningAnimation.visibility = View.GONE
                binding.processingAnimation.visibility = View.VISIBLE
                binding.speakingAnimation.visibility = View.GONE
            }
            is VoiceAssistantState.Speaking -> {
                binding.statusText.text = "Speaking..."
                binding.listeningAnimation.visibility = View.GONE
                binding.processingAnimation.visibility = View.GONE
                binding.speakingAnimation.visibility = View.VISIBLE
            }
            is VoiceAssistantState.Connecting -> {
                binding.microphoneButton.isEnabled = false
                binding.statusText.text = "Connecting to Live AI..."
                binding.listeningAnimation.visibility = View.GONE
                binding.processingAnimation.visibility = View.VISIBLE
                binding.speakingAnimation.visibility = View.GONE
            }
            is VoiceAssistantState.LiveConversation -> {
                binding.microphoneButton.isEnabled = true
                binding.statusText.text = "🚨 LIVE Emergency Assistant Active"
                binding.listeningAnimation.visibility = View.VISIBLE
                binding.processingAnimation.visibility = View.GONE
                binding.speakingAnimation.visibility = View.GONE
            }
            is VoiceAssistantState.Error -> {
                binding.statusText.text = "Error: ${state.message}"
                binding.listeningAnimation.visibility = View.GONE
                binding.processingAnimation.visibility = View.GONE
                binding.speakingAnimation.visibility = View.GONE
            }
        }
    }

    private fun handleMicrophoneClick() {
        viewModel.startListening()
    }

    private fun checkAndRequestPermissions() {
        viewModel.checkPermissions()

        val missingPermissions = viewModel.getMissingPermissions()
        if (missingPermissions.isNotEmpty()) {
            // Show rationale dialog
            showPermissionRationaleDialog(missingPermissions)
        } else {
            // All permissions granted, initialize
            viewModel.initialize()
        }
    }

    private fun showPermissionRationaleDialog(permissions: List<String>) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Required")
            .setMessage(
                "The Voice Assistant needs the following permissions to function properly:\n\n" +
                permissions.joinToString("\n") { permission ->
                    "• " + viewModel.getPermissionRationale(permission)
                }
            )
            .setPositiveButton("Grant") { _, _ ->
                permissionLauncher.launch(permissions.toTypedArray())
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Voice Assistant requires permissions to work",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Permissions Denied")
            .setMessage(
                "The Voice Assistant cannot function without the required permissions. " +
                "Please grant permissions in app settings."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

