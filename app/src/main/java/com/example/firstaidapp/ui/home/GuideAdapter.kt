package com.example.firstaidapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.databinding.ItemGuideCardBinding
import androidx.core.content.ContextCompat
import com.example.firstaidapp.R

class GuideAdapter(
    private val onGuideClick: (FirstAidGuide) -> Unit,
    private val onViewDemoClick: (String) -> Unit
) : ListAdapter<FirstAidGuide, GuideAdapter.GuideViewHolder>(GuideDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GuideViewHolder(
        private val binding: ItemGuideCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(guide: FirstAidGuide) {
            binding.tvGuideTitle.text = guide.title
            binding.tvGuideDescription.text = guide.description
            binding.tvCategory.text = guide.category

            // Set guide icon based on category
            val iconResource = GuideIconMapper.getIconForGuide(guide.title)
            binding.ivGuideImage.setImageResource(iconResource)

            // Set severity indicator with color coding
            binding.tvSeverity.text = guide.severity
            when (guide.severity) {
                "CRITICAL" -> binding.tvSeverity.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.severity_critical)
                )
                "HIGH" -> binding.tvSeverity.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.severity_high)
                )
                "MEDIUM" -> binding.tvSeverity.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.severity_medium)
                )
                else -> binding.tvSeverity.setBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.severity_low)
                )
            }

            // Removed all animations - immediate click response
            binding.root.setOnClickListener {
                onGuideClick(guide)
            }

            // Set click listener for View Demo button
            binding.tvViewDemo.setOnClickListener {
                if (guide.youtubeLink?.isNotEmpty() == true) {
                    onViewDemoClick(guide.youtubeLink ?: "")
                }
            }

            // Show/hide demo button based on YouTube link availability
            binding.tvViewDemo.visibility = if (guide.youtubeLink?.isNotEmpty() == true) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
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
