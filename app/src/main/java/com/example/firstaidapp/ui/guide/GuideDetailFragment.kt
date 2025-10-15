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
import com.example.firstaidapp.databinding.FragmentGuideDetailBinding
import com.example.firstaidapp.ui.home.PhotoMapper
import java.io.IOException

class GuideDetailFragment : Fragment() {

    private var _binding: FragmentGuideDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GuideDetailViewModel
    private lateinit var stepsAdapter: GuideStepsAdapter

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
        stepsAdapter = GuideStepsAdapter()

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
                binding.tvSeverity.text = it.severity
                binding.tvDescription.text = it.description
                binding.tvWarnings.text = it.warnings.joinToString("\n• ", "• ")
                binding.tvWhenToCall.text = it.whenToCallEmergency

                stepsAdapter.submitList(it.steps)

                // Load demonstration photo for this guide
                loadGuidePhoto(it.title)
            }
        }
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

        // Full photo view click listener
        binding.tvViewFullPhoto.setOnClickListener {
            showFullSizePhoto()
        }

        // Handle photo tap for full-size viewing
        binding.ivGuidePhoto.setOnClickListener {
            showFullSizePhoto()
        }
    }

    private fun showFullSizePhoto() {
        // Create and show a full-screen image dialog
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val imageView = ImageView(requireContext())
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

        // Set the same image as the guide photo
        binding.ivGuidePhoto.drawable?.let { drawable ->
            imageView.setImageDrawable(drawable)
        }

        // Close dialog on tap
        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(imageView)
        dialog.show()
    }

    private fun makeEmergencyCall() {
        try {
            val intent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:911".toUri() // Using 911 as default emergency number
            }
            startActivity(intent)
        } catch (_: Exception) {
            // If calling fails, open dialer instead
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = "tel:911".toUri()
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
