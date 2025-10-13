package com.example.firstaidapp.ui.home

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.databinding.FragmentHomeBinding
import com.example.firstaidapp.ui.search.SearchResultsAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var guideAdapter: GuideAdapter
    private lateinit var searchResultsAdapter: SearchResultsAdapter

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
        setupSearch()
    }

    private fun setupViewModel() {
        val factory = HomeViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }

    private fun setupRecyclerView() {
        guideAdapter = GuideAdapter { guide ->
            // Navigate to guide detail with the selected guide
            val action = HomeFragmentDirections.actionHomeToGuideDetail(guide.id)
            findNavController().navigate(action)
        }

        searchResultsAdapter = SearchResultsAdapter { guide ->
            // Navigate to guide detail with the selected guide ID
            val action = HomeFragmentDirections.actionHomeToGuideDetail(guide.id)
            findNavController().navigate(action)
        }

        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = guideAdapter
        }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchResultsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.featuredGuides.observe(viewLifecycleOwner) { guides ->
            guideAdapter.submitList(guides)
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            searchResultsAdapter.submitList(results)
        }
    }

    private fun setupClickListeners() {
        // Enhanced emergency call button with pulsing animation
        binding.cardEmergencyCall.setOnClickListener { view ->
            // Stop any existing animations
            view.clearAnimation()

            // Create pulsing effect for emergency button
            val pulseAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_press)
            view.startAnimation(pulseAnimation)

            // Add haptic feedback for emergency action
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            }

            // Delay call to let animation complete
            view.postDelayed({
                viewModel.callEmergencyServices(requireContext())
            }, 150)
        }

        // Enhanced settings button with smooth animation
        binding.btnSettings.setOnClickListener { view ->
            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_scale_in)
            view.startAnimation(animation)

            view.postDelayed({
                findNavController().navigate(HomeFragmentDirections.actionHomeToSettings())
            }, 100)
        }
    }

    private fun setupSearch() {
        binding.etSearchGuides.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()

                // Animate search transitions
                if (query.isNotEmpty()) {
                    // Fade out categories, fade in search results
                    binding.rvCategories.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.rvCategories.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.VISIBLE
                            binding.rvSearchResults.alpha = 0f
                            binding.rvSearchResults.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start()
                        }
                        .start()

                    viewModel.searchGuides(query)
                } else {
                    // Fade out search results, fade in categories
                    binding.rvSearchResults.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            binding.rvSearchResults.visibility = View.GONE
                            binding.rvCategories.visibility = View.VISIBLE
                            binding.rvCategories.alpha = 0f
                            binding.rvCategories.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .start()
                        }
                        .start()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Add focus animation to search bar
        binding.etSearchGuides.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                view.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(200)
                    .start()
            } else {
                view.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
