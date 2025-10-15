package com.example.firstaidapp.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            viewModel.callEmergencyServices(requireContext())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
