package com.example.firstaidapp.ui.guides

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.databinding.FragmentAllGuidesBinding
import com.example.firstaidapp.ui.home.CategorizedGuideAdapter
import com.example.firstaidapp.ui.home.CategoryItem

/**
 * AllGuidesFragment - Displays all available first aid guides
 */
class AllGuidesFragment : Fragment() {

    private var _binding: FragmentAllGuidesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AllGuidesViewModel
    private lateinit var categorizedAdapter: CategorizedGuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllGuidesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupSearchFunctionality()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AllGuidesViewModel::class.java]
    }

    private fun setupRecyclerView() {
        categorizedAdapter = CategorizedGuideAdapter(
            onGuideClick = { guide ->
                // Navigate to guide detail
                try {
                    val action = AllGuidesFragmentDirections.actionAllGuidesToGuideDetail(guide.id)
                    findNavController().navigate(action)
                } catch (e: Exception) {
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

        binding.rvGuides.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categorizedAdapter
            itemAnimator = null
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            layoutTransition = null
        }
    }

    private fun setupObservers() {
        // Observe categorized items
        viewModel.categorizedItems.observe(viewLifecycleOwner) { categorizedItems ->
            categorizedAdapter.submitList(categorizedItems)
        }

        // Observe search results
        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNotEmpty()) {
                val searchItems = searchResults.map { CategoryItem.GuideItem(it) }
                categorizedAdapter.submitList(searchItems)
            } else {
                viewModel.categorizedItems.value?.let { categorizedItems ->
                    categorizedAdapter.submitList(categorizedItems)
                }
            }
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
            Log.e("AllGuidesFragment", "Failed to open YouTube video", e)
            Toast.makeText(requireContext(), "Unable to open video", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

