package com.example.firstaidapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.firstaidapp.R
import com.example.firstaidapp.data.database.AppDatabase
import com.example.firstaidapp.data.repository.GuideRepository
import com.example.firstaidapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var repository: GuideRepository

    private val emergencyTips = listOf(
        "Always call emergency services in life-threatening situations",
        "Stay calm and assess the situation before taking action",
        "Never move an injured person unless they're in immediate danger",
        "Check for breathing and pulse before starting CPR",
        "Apply direct pressure to stop severe bleeding",
        "Keep emergency contact numbers easily accessible"
    )

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                makeEmergencyCall()
            } else {
                Toast.makeText(requireContext(), "Permission denied. Opening dialer instead.", Toast.LENGTH_SHORT).show()
                makeEmergencyDialerCall()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repository
        val database = AppDatabase.getDatabase(requireContext())
        repository = GuideRepository(
            database.guideDao(),
            database.contactDao(),
            database.searchDao()
        )

        setupClickListeners()
        setupEmergencyTip()
    }

    private fun setupClickListeners() {
        // Emergency call button
        binding.btnEmergencyCall.setOnClickListener {
            Log.d("HomeFragment", "Emergency call button clicked")
            makeEmergencyCall()
        }

        // Quick Action Cards - Navigate to specific guides
        binding.cardCPR.setOnClickListener {
            navigateToGuide("CPR")
        }

        binding.cardChoking.setOnClickListener {
            navigateToGuide("Choking")
        }

        binding.cardBurns.setOnClickListener {
            navigateToGuide("Burns")
        }

        binding.cardBleeding.setOnClickListener {
            navigateToGuide("Severe Bleeding")
        }

        // Emergency Scenario Cards
        binding.cardHeartAttack.setOnClickListener {
            navigateToGuide("Heart Attack")
        }

        binding.cardStroke.setOnClickListener {
            navigateToGuide("Stroke")
        }

        binding.cardAllergicReaction.setOnClickListener {
            navigateToGuide("Allergic Reactions")
        }

        // AI Assistant Quick Access
        binding.cardAIAssistant.setOnClickListener {
            try {
                findNavController().navigate(R.id.navigation_voice_assistant)
            } catch (e: Exception) {
                Log.e("HomeFragment", "Failed to navigate to AI Assistant", e)
                Toast.makeText(requireContext(), "Unable to open AI Assistant", Toast.LENGTH_SHORT).show()
            }
        }

        // Emergency Tip Card - Rotate tips on click
        binding.cardEmergencyTip.setOnClickListener {
            showRandomTip()
        }
    }

    private fun setupEmergencyTip() {
        // Show initial tip
        showRandomTip()
    }

    private fun showRandomTip() {
        val randomTip = emergencyTips.random()
        binding.tvEmergencyTip.text = randomTip
    }

    private fun navigateToGuide(guideTitle: String) {
        lifecycleScope.launch {
            try {
                // Search for the specific guide
                val guides = repository.searchGuidesList(guideTitle)

                if (guides.isNotEmpty()) {
                    // Find the exact match or the first result
                    val guide = guides.firstOrNull { it.title.contains(guideTitle, ignoreCase = true) }
                        ?: guides.first()

                    // Navigate to guide detail with the guide ID
                    val action = HomeFragmentDirections.actionHomeToGuideDetail(guide.id)
                    findNavController().navigate(action)
                    Log.d("HomeFragment", "Navigating to guide: ${guide.title} with ID: ${guide.id}")
                } else {
                    // If guide not found, navigate to All Guides
                    Log.w("HomeFragment", "Guide not found: $guideTitle, navigating to All Guides")
                    findNavController().navigate(R.id.navigation_all_guides)
                    Toast.makeText(requireContext(), "Guide not found. Showing all guides...", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "Failed to navigate to guide: $guideTitle", e)
                Toast.makeText(requireContext(), "Unable to open guide", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun makeEmergencyCall() {
        // Add comprehensive null checks and error handling
        if (_binding == null || !isAdded) return

        try {

            // Check for call permission
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("HomeFragment", "CALL_PHONE permission granted, making direct call")
                    // Show user feedback
                    Toast.makeText(requireContext(), "Calling Emergency Services (112)...", Toast.LENGTH_SHORT).show()

                    // Direct call
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = "tel:112".toUri()
                    }
                    if (callIntent.resolveActivity(requireContext().packageManager) != null) {
                        Log.d("HomeFragment", "Starting emergency call activity")
                        startActivity(callIntent)
                    } else {
                        Log.e("HomeFragment", "No app to handle call intent, falling back to dialer")
                        makeEmergencyDialerCall()
                    }
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE) -> {
                    Log.d("HomeFragment", "Showing permission rationale")
                    Toast.makeText(requireContext(), "Call permission is needed to make emergency calls.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
                else -> {
                    Log.d("HomeFragment", "Requesting CALL_PHONE permission")
                    requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                }
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Failed to initiate emergency call", e)
            Toast.makeText(requireContext(), "Direct call failed. Opening dialer.", Toast.LENGTH_SHORT).show()
            makeEmergencyDialerCall()
        }
    }

    private fun makeEmergencyDialerCall() {
        try {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:112".toUri()
            }
            startActivity(dialIntent)
            Log.d("HomeFragment", "Emergency dialer opened successfully")
        } catch (e: Exception) {
            Log.e("HomeFragment", "Failed to open emergency dialer", e)
            Toast.makeText(requireContext(), "Unable to initiate call", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
