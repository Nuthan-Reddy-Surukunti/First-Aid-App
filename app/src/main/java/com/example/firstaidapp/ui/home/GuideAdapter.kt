package com.example.firstaidapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.data.models.FirstAidGuide
import com.example.firstaidapp.databinding.ItemGuideCardBinding
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.firstaidapp.R

class GuideAdapter(
    private val onGuideClick: (FirstAidGuide) -> Unit
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

            // Add smooth entrance animation with staggered delay
            val animation = AnimationUtils.loadAnimation(binding.root.context, R.anim.card_slide_up)
            animation.startOffset = (adapterPosition * 100).toLong() // Stagger animation
            binding.root.startAnimation(animation)

            // Add interactive button press effect
            binding.root.setOnClickListener { view ->
                val pressAnimation = AnimationUtils.loadAnimation(view.context, R.anim.button_press)
                view.startAnimation(pressAnimation)

                // Delay the navigation to let animation complete
                view.postDelayed({
                    onGuideClick(guide)
                }, 100)
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
