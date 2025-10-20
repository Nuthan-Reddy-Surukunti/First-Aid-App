package com.example.firstaidapp.ui.guide

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.R
import com.example.firstaidapp.databinding.FragmentGuideDetailBinding
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.ui.home.PhotoMapper
import com.example.firstaidapp.utils.Constants
import java.io.IOException

class GuideDetailFragment : Fragment() {

    private var _binding: FragmentGuideDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GuideDetailViewModel
    private lateinit var stepsAdapter: GuideStepsAdapter
    private var isDetailedInstructionsVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuideDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Get guide ID from arguments
        val guideId = arguments?.getString("guide_id") ?: ""
        if (guideId.isNotEmpty()) {
            viewModel.loadGuide(guideId)
        }
    }

    private fun setupViewModel() {
        val factory = GuideDetailViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[GuideDetailViewModel::class.java]
    }

    private fun setupRecyclerView() {
        // Initialize adapter without guide name first, will be updated when guide loads
        stepsAdapter = GuideStepsAdapter(
            onStepCompleted = { step ->
                // Handle step completion
                viewModel.markStepCompleted(step)
            },
            onVideoPlay = { videoUrl ->
                // Handle video playback
                playVideo(videoUrl)
            },
            onStepExpanded = { step ->
                // Handle step expansion analytics
                // Could track which steps users expand for analytics
            }
        )

        binding.rvSteps.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stepsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.guide.observe(viewLifecycleOwner) { guide ->
            guide?.let {
                binding.tvGuideTitle.text = it.title
                binding.tvCategory.text = it.category
                binding.tvWarnings.text = it.warnings.joinToString("\n• ", "• ")
                binding.tvWhenToCall.text = it.whenToCallEmergency

                // Prepare detailed instructions content but keep hidden by default
                binding.tvDetailedDescription.text = createDetailedInstructions(it)
                // Keep detailed instructions hidden initially for emergency situations
                binding.cvDetailedInstructions.visibility = View.GONE
                isDetailedInstructionsVisible = false

                binding.tvWarnings.text = it.warnings.joinToString("\n• ", "• ")
                binding.tvWhenToCall.text = it.whenToCallEmergency

                // Create new adapter with guide name for better image context
                stepsAdapter = GuideStepsAdapter(
                    onStepCompleted = { step ->
                        viewModel.markStepCompleted(step)
                    },
                    onVideoPlay = { videoUrl ->
                        playVideo(videoUrl)
                    },
                    onStepExpanded = { step ->
                        // Analytics tracking for step expansion
                    },
                    guideName = it.title // Pass guide name for context-aware image mapping
                )

                binding.rvSteps.adapter = stepsAdapter
                stepsAdapter.submitList(it.steps)

                // Load demonstration photo for this guide
                loadGuidePhoto(it.title)
            }
        }
    }

    private fun createDetailedInstructions(guide: FirstAidGuide): String {
        val sb = StringBuilder()

        // Add guide description
        sb.append(guide.description)
        sb.append("\n\n")

        // Add preparation information
        sb.append("📋 Preparation:\n")
        sb.append("• Ensure the scene is safe before approaching\n")
        sb.append("• Check for responsiveness and breathing\n")
        sb.append("• Call for help if needed\n")
        sb.append("• Gather any required materials\n\n")

        // Add general guidelines based on guide title
        when {
            guide.title.contains("CPR", ignoreCase = true) -> {
                sb.append("🫀 CPR Guidelines:\n")
                sb.append("• Push hard and fast at least 2 inches deep\n")
                sb.append("• Allow complete chest recoil between compressions\n")
                sb.append("• Minimize interruptions\n")
                sb.append("• Switch providers every 2 minutes to avoid fatigue\n\n")
            }
            guide.title.contains("Choking", ignoreCase = true) -> {
                sb.append("🫁 Choking Response:\n")
                sb.append("• Encourage coughing if the person can still breathe\n")
                sb.append("• Be prepared to catch the person if they become unconscious\n")
                sb.append("• Continue until object is expelled or person becomes unconscious\n\n")
            }
            guide.title.contains("Burns", ignoreCase = true) -> {
                sb.append("🔥 Burn Care Principles:\n")
                sb.append("• Cool the burn immediately with cool running water\n")
                sb.append("• Remove from heat source safely\n")
                sb.append("• Do not use ice, butter, or other home remedies\n")
                sb.append("• Protect the area from further damage\n\n")
            }
        }

        // Add timing information
        if (guide.estimatedTimeMinutes > 0) {
            sb.append("⏱️ Estimated Time: ${guide.estimatedTimeMinutes} minutes\n")
        }

        // Add difficulty level
        sb.append("📊 Difficulty Level: ${guide.difficulty}\n\n")

        // Add when to seek professional help
        sb.append("🚨 Seek Professional Help When:\n")
        guide.warnings.forEach { warning ->
            sb.append("• $warning\n")
        }

        return sb.toString()
    }

    private fun loadGuidePhoto(guideTitle: String) {
        val photoPath = PhotoMapper.getPhotoForGuide(guideTitle)

        if (photoPath != null) {
            try {
                // Try to load the photo from assets
                val inputStream = requireContext().assets.open(photoPath)
                val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)

                if (drawable != null) {
                    binding.ivGuidePhoto.setImageDrawable(drawable)
                    binding.cvPhotoDemo.visibility = View.VISIBLE
                } else {
                    // If photo loading fails, hide the photo section
                    binding.cvPhotoDemo.visibility = View.GONE
                }

                inputStream.close()

            } catch (_: IOException) {
                // Photo file doesn't exist, hide the photo section
                binding.cvPhotoDemo.visibility = View.GONE
            }
        } else {
            // No photo mapping found, hide the photo section
            binding.cvPhotoDemo.visibility = View.GONE
        }
    }

    private fun setupClickListeners() {
        // Emergency call button
        binding.btnCallEmergency.setOnClickListener {
            makeEmergencyCall()
        }

        // Simple photo tap feedback (optional - can be removed entirely)
        binding.ivGuidePhoto.setOnClickListener {
            // Photo is already fully visible, no action needed
        }

        // Toggle detailed instructions visibility
        binding.btnToggleDetails.setOnClickListener {
            toggleDetailedInstructions()
        }
    }

    private fun toggleDetailedInstructions() {
        isDetailedInstructionsVisible = !isDetailedInstructionsVisible

        if (isDetailedInstructionsVisible) {
            binding.cvDetailedInstructions.visibility = View.VISIBLE
            binding.btnToggleDetails.text = "Hide Detailed Instructions"
            binding.btnToggleDetails.setIconResource(R.drawable.ic_expand_less)
        } else {
            binding.cvDetailedInstructions.visibility = View.GONE
            binding.btnToggleDetails.text = "Show Detailed Instructions"
            binding.btnToggleDetails.setIconResource(R.drawable.ic_expand_more)
        }
    }



    private fun makeEmergencyCall() {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:${Constants.EMERGENCY_NUMBER_IN}".toUri()
            }
            startActivity(intent)
        } catch (_: Exception) {
            // If calling fails, open dialer instead
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:${Constants.EMERGENCY_NUMBER_IN}".toUri()
            }
            startActivity(intent)
        }
    }

    private fun playVideo(videoUrl: String) {
        try {
            // Create intent to play video
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(videoUrl.toUri(), "video/*")
            }

            // Check if there's an app that can handle video playback
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            } else {
                // If no video player available, open in browser
                val browserIntent = Intent(Intent.ACTION_VIEW, videoUrl.toUri())
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            // Handle error - could show a toast or log the error
            android.util.Log.e("GuideDetailFragment", "Error playing video: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
