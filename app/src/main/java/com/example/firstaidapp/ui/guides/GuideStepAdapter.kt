package com.example.firstaidapp.ui.guides

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.data.models.StepType
import com.example.firstaidapp.databinding.ItemGuideStepBinding

class GuideStepAdapter(
    private val onStepCompleted: (GuideStep) -> Unit,
    private val onStepExpanded: (GuideStep, Boolean) -> Unit
) : ListAdapter<GuideStep, GuideStepAdapter.StepViewHolder>(StepDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemGuideStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StepViewHolder(
        private val binding: ItemGuideStepBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(step: GuideStep) {
            binding.apply {
                // Set step number
                tvStepNumber.text = step.stepNumber.toString()

                // Set step title and description
                tvStepTitle.text = step.title
                tvStepDescription.text = step.description

                // Set step type icon based on step type
                when (step.stepType) {
                    StepType.CHECK -> ivStepTypeIcon.setImageResource(R.drawable.ic_search)
                    StepType.ACTION -> ivStepTypeIcon.setImageResource(R.drawable.ic_action)
                    StepType.CALL -> ivStepTypeIcon.setImageResource(R.drawable.ic_phone)
                    StepType.REPEAT -> ivStepTypeIcon.setImageResource(R.drawable.ic_repeat)
                    StepType.ASSESSMENT -> ivStepTypeIcon.setImageResource(R.drawable.ic_search)
                    StepType.EMERGENCY_CALL -> ivStepTypeIcon.setImageResource(R.drawable.ic_phone)
                    StepType.POSITIONING -> ivStepTypeIcon.setImageResource(R.drawable.ic_action)
                    StepType.MONITORING -> ivStepTypeIcon.setImageResource(R.drawable.ic_action)
                    StepType.SAFETY -> ivStepTypeIcon.setImageResource(R.drawable.ic_warning)
                    StepType.FOLLOW_UP -> ivStepTypeIcon.setImageResource(R.drawable.ic_action)
                }

                // Show/hide duration if available
                step.iconRes?.let {
                    ivStepTypeIcon.setImageResource(it)
                }

                // Show/hide duration if available
                if (!step.duration.isNullOrEmpty()) {
                    tvDuration.visibility = View.VISIBLE
                    tvDuration.text = step.duration
                } else {
                    tvDuration.visibility = View.GONE
                }

                // Show critical warning indicator for critical steps
                ivCriticalWarning.visibility = if (step.isCritical) View.VISIBLE else View.GONE

                // Handle step image if available
                step.imageRes?.let { imageResId ->
                    cardStepImage.visibility = View.VISIBLE
                    ivStepImage.setImageResource(imageResId)
                } ?: run {
                    cardStepImage.visibility = View.GONE
                }

                // Handle detailed instructions
                if (!step.detailedInstructions.isNullOrEmpty()) {
                    tvDetailedInstructions.text = step.detailedInstructions
                } else {
                    layoutDetailedInstructions.visibility = View.GONE
                }

                // Handle required tools
                if (!step.requiredTools.isNullOrEmpty()) {
                    layoutRequiredTools.visibility = View.VISIBLE
                    // TODO: Populate tools flexbox layout
                } else {
                    layoutRequiredTools.visibility = View.GONE
                }

                // Handle tips
                if (!step.tips.isNullOrEmpty()) {
                    layoutTips.visibility = View.VISIBLE
                    // TODO: Populate tips section
                } else {
                    layoutTips.visibility = View.GONE
                }

                // Handle warnings if available
                if (!step.warnings.isNullOrEmpty()) {
                    // Show warning section if available
                    step.warnings?.forEach { warning ->
                        // Add warning to UI (implementation depends on layout)
                    }
                }

                // Set up click listeners for expansion/interaction
                setupStepInteractions(step)

                // Apply entrance animation
                applyEntranceAnimation()
            }
        }

        private fun setupStepInteractions(step: GuideStep) {
            binding.apply {
                // Expandable content toggle
                cardStep.setOnClickListener {
                    toggleExpandedContent(step)
                }

                // Step completion marking
                tvStepNumber.setOnClickListener {
                    markStepCompleted(step)
                }

                // Video play button (if applicable)
                fabPlayVideo.setOnClickListener {
                    // TODO: Implement video playback
                }
            }
        }

        private fun toggleExpandedContent(step: GuideStep) {
            binding.apply {
                val isExpanded = layoutDetailedInstructions.visibility == View.VISIBLE

                if (isExpanded) {
                    // Collapse with animation
                    layoutDetailedInstructions.animate()
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction {
                            layoutDetailedInstructions.visibility = View.GONE
                        }
                        .start()
                } else {
                    // Expand with animation
                    layoutDetailedInstructions.visibility = View.VISIBLE
                    layoutDetailedInstructions.alpha = 0f
                    layoutDetailedInstructions.animate()
                        .alpha(1f)
                        .setDuration(200)
                        .start()
                }

                onStepExpanded(step, !isExpanded)
            }
        }

        private fun markStepCompleted(step: GuideStep) {
            binding.apply {
                // Visual feedback for completion
                tvStepNumber.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(150)
                    .withEndAction {
                        tvStepNumber.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()

                onStepCompleted(step)
            }
        }

        private fun applyEntranceAnimation() {
            binding.cardStep.apply {
                alpha = 0f
                translationY = 50f
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay((bindingAdapterPosition * 100).toLong())
                    .start()
            }
        }
    }

    class StepDiffCallback : DiffUtil.ItemCallback<GuideStep>() {
        override fun areItemsTheSame(oldItem: GuideStep, newItem: GuideStep): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GuideStep, newItem: GuideStep): Boolean {
            return oldItem == newItem
        }
    }
}
