package com.example.firstaidapp.ui.guide

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.databinding.FragmentGuideDetailBinding

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
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCallEmergency.setOnClickListener {
            // Use ACTION_DIAL instead of ACTION_CALL to avoid permission issues
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:911")
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
