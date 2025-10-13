package com.example.firstaidapp.ui.search

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
import com.example.firstaidapp.R
import com.example.firstaidapp.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel
    private lateinit var searchAdapter: SearchResultsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSearchBar()
        setupObservers()
        setupClickListeners()
    }

    private fun setupViewModel() {
        val factory = SearchViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private fun setupRecyclerView() {
        searchAdapter = SearchResultsAdapter { guide ->
            // Navigate to guide detail using bundle instead of SafeArgs temporarily
            val bundle = Bundle().apply {
                putString("guide_id", guide.id)
            }
            findNavController().navigate(R.id.action_search_to_guide_detail, bundle)
        }

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString() ?: ""
                viewModel.searchGuides(query)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isEmpty()) {
                binding.rvSearchResults.visibility = View.GONE
                binding.tvNoResults.visibility = View.VISIBLE
            } else {
                binding.rvSearchResults.visibility = View.VISIBLE
                binding.tvNoResults.visibility = View.GONE
                searchAdapter.submitList(results)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSettings.setOnClickListener {
            // Navigate to Settings screen
            findNavController().navigate(SearchFragmentDirections.actionSearchToSettings())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
