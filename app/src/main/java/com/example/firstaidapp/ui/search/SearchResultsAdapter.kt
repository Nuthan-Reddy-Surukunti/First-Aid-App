package com.example.firstaidapp.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.databinding.ItemSearchResultBinding

class SearchResultsAdapter(
    private val onGuideClick: (FirstAidGuide) -> Unit
) : ListAdapter<FirstAidGuide, SearchResultsAdapter.SearchResultViewHolder>(GuideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val binding = ItemSearchResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchResultViewHolder(
        private val binding: ItemSearchResultBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(guide: FirstAidGuide) {
            binding.tvGuideTitle.text = guide.title
            binding.tvGuideDescription.text = guide.description
            binding.tvCategory.text = guide.category

            binding.root.setOnClickListener {
                onGuideClick(guide)
            }
        }
    }

    class GuideDiffCallback : DiffUtil.ItemCallback<FirstAidGuide>() {
        override fun areItemsTheSame(oldItem: FirstAidGuide, newItem: FirstAidGuide): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FirstAidGuide, newItem: FirstAidGuide): Boolean {
            return oldItem == newItem
        }
    }
}
