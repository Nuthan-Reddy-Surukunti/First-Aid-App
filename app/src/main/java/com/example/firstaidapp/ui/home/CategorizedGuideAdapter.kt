package com.example.firstaidapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.databinding.ItemGuideCardBinding
import androidx.core.content.ContextCompat

class CategorizedGuideAdapter(
    private val onGuideClick: (FirstAidGuide) -> Unit,
    private val onCategoryClick: (String) -> Unit,
    private val onViewDemoClick: (String) -> Unit
) : ListAdapter<CategoryItem, RecyclerView.ViewHolder>(CategoryItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_CATEGORY_HEADER = 0
        private const val VIEW_TYPE_GUIDE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryItem.CategoryHeader -> VIEW_TYPE_CATEGORY_HEADER
            is CategoryItem.GuideItem -> VIEW_TYPE_GUIDE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
                CategoryHeaderViewHolder(view)
            }
            VIEW_TYPE_GUIDE_ITEM -> {
                val binding = ItemGuideCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                GuideItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryItem.CategoryHeader -> {
                (holder as CategoryHeaderViewHolder).bind(item)
            }
            is CategoryItem.GuideItem -> {
                (holder as GuideItemViewHolder).bind(item.guide)
            }
        }
    }

    inner class CategoryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategoryTitle: TextView = itemView.findViewById(R.id.tvCategoryTitle)
        private val tvCategoryDescription: TextView = itemView.findViewById(R.id.tvCategoryDescription)
        private val tvCategoryIcon: TextView = itemView.findViewById(R.id.tvCategoryIcon)
        private val tvGuideCount: TextView = itemView.findViewById(R.id.tvGuideCount)
        private val tvExpandIcon: TextView = itemView.findViewById(R.id.tvExpandIcon)

        fun bind(categoryHeader: CategoryItem.CategoryHeader) {
            tvCategoryTitle.text = categoryHeader.title
            tvCategoryDescription.text = categoryHeader.description
            tvCategoryIcon.text = categoryHeader.icon
            tvGuideCount.text = "${categoryHeader.guideCount} guides"

            // Set expand/collapse icon
            tvExpandIcon.text = if (categoryHeader.isExpanded) "▼" else "▶"

            itemView.setOnClickListener {
                onCategoryClick(categoryHeader.title)
            }
        }
    }

    inner class GuideItemViewHolder(
        private val binding: ItemGuideCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(guide: FirstAidGuide) {
            binding.tvGuideTitle.text = guide.title
            binding.tvGuideDescription.text = guide.description
            binding.tvCategory.text = guide.category
            binding.tvSeverity.text = guide.severity

            // Set guide icon based on category
            val iconResource = GuideIconMapper.getIconForGuide(guide.title)
            binding.ivGuideImage.setImageResource(iconResource)

            // Set severity color
            val severityColor = when (guide.severity.uppercase()) {
                "CRITICAL" -> R.color.severity_critical
                "HIGH" -> R.color.severity_high
                "MODERATE" -> R.color.severity_medium
                else -> R.color.severity_low
            }
            binding.tvSeverity.setBackgroundResource(severityColor)

            // Set click listener for navigation
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

    class CategoryItemDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return when {
                oldItem is CategoryItem.CategoryHeader && newItem is CategoryItem.CategoryHeader -> {
                    oldItem.title == newItem.title
                }
                oldItem is CategoryItem.GuideItem && newItem is CategoryItem.GuideItem -> {
                    oldItem.guide.id == newItem.guide.id
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
