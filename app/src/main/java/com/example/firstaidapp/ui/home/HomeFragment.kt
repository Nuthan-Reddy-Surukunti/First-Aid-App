package com.example.firstaidapp.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var categorizedAdapter: CategorizedGuideAdapter

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

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
        setupSearchFunctionality()
    }

    private fun setupViewModel() {
        val factory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        categorizedAdapter = CategorizedGuideAdapter(
            onGuideClick = { guide ->
                // Direct navigation without any animations
                try {
                    val action = HomeFragmentDirections.actionHomeToGuideDetail(guide.id)
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    // Fallback navigation if directions not available
                    e.printStackTrace()
                }
            },
            onCategoryClick = { categoryTitle ->
                // Toggle category expansion
                viewModel.toggleCategory(categoryTitle)
            },
            onViewDemoClick = { youtubeLink ->
                // Open YouTube video
                openYouTubeVideo(youtubeLink)
            }
        )

        // Changed to LinearLayoutManager for better category display
        binding.rvGuides.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categorizedAdapter

            // Completely disable all RecyclerView animations
            itemAnimator = null

            // Disable overscroll effects and animations
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            // Disable layout transition animations
            layoutTransition = null
        }
    }

    private fun setupObservers() {
        // Observe categorized items instead of featured guides
        viewModel.categorizedItems.observe(viewLifecycleOwner) { categorizedItems ->
            categorizedAdapter.submitList(categorizedItems)
        }

        // Observe search results - when searching, show flat list instead of categories
        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNotEmpty()) {
                // Convert search results to CategoryItem.GuideItem for display
                val searchItems = searchResults.map { CategoryItem.GuideItem(it) }
                categorizedAdapter.submitList(searchItems)
            } else {
                // If search is cleared, go back to categorized view
                viewModel.categorizedItems.value?.let { categorizedItems ->
                    categorizedAdapter.submitList(categorizedItems)
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Emergency call button
        binding.btnEmergencyCall.setOnClickListener {
            Log.d("HomeFragment", "Emergency call button clicked")
            makeEmergencyCall()
        }
    }

    private fun makeEmergencyCall() {
        // Add comprehensive null checks and error handling
        if (_binding == null || !isAdded) return

        try {
            // Enhanced call animation with haptic feedback
            binding.root.performHapticFeedback(android.view.HapticFeedbackConstants.CONFIRM)

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

    private fun setupSearchFunctionality() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchGuides(query)
                } else {
                    viewModel.clearSearch()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun openYouTubeVideo(youtubeLink: String) {
        try {
            // Try to open with YouTube app first
            val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink)).apply {
                setPackage("com.google.android.youtube")
            }

            if (youtubeIntent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(youtubeIntent)
            } else {
                // Fallback to web browser if YouTube app is not installed
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
                startActivity(webIntent)
            }
        } catch (e: Exception) {
            Log.e("HomeFragment", "Failed to open YouTube video", e)
            Toast.makeText(requireContext(), "Unable to open video", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
