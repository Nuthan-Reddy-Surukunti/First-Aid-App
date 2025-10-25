package com.example.firstaidapp.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firstaidapp.R
import com.example.firstaidapp.data.models.GuideStep
import com.example.firstaidapp.data.models.StepType
import com.example.firstaidapp.databinding.ItemGuideStepBinding
import com.google.android.material.chip.Chip

class GuideStepsAdapter(
    private val onStepCompleted: (GuideStep) -> Unit = {},
    private val onVideoPlay: (String) -> Unit = {},
    private val onStepExpanded: (GuideStep) -> Unit = {},
    private val guideName: String? = null // Add guide name for context
) : ListAdapter<GuideStep, GuideStepsAdapter.StepViewHolder>(StepDiffCallback()) {

    private var expandedSteps = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepViewHolder {
        val binding = ItemGuideStepBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StepViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StepViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onViewAttachedToWindow(holder: StepViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.playEntranceAnimation()
    }

    inner class StepViewHolder(private val binding: ItemGuideStepBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(step: GuideStep, position: Int) {
            binding.apply {
                // Basic step info
                tvStepNumber.text = step.stepNumber.toString()
                tvStepTitle.text = step.title
                tvStepDescription.text = step.description

                // Set step type icon
                val iconResource = when (step.stepType) {
                    StepType.CHECK -> R.drawable.ic_visibility
                    StepType.ACTION -> R.drawable.ic_action
                    StepType.CALL -> R.drawable.ic_phone
                    StepType.REPEAT -> R.drawable.ic_repeat
                    StepType.ASSESSMENT -> R.drawable.ic_visibility
                    StepType.EMERGENCY_CALL -> R.drawable.ic_phone
                    StepType.POSITIONING -> R.drawable.ic_action
                    StepType.MONITORING -> R.drawable.ic_action
                    StepType.SAFETY -> R.drawable.ic_warning
                    StepType.FOLLOW_UP -> R.drawable.ic_action
                    StepType.WAIT -> R.drawable.ic_action
                    StepType.OBSERVE -> R.drawable.ic_visibility
                }
                ivStepTypeIcon.setImageResource(iconResource)

                // Use iconRes if provided, otherwise use step type default
                step.iconRes?.let {
                    ivStepTypeIcon.setImageResource(it)
                }

                // Step Illustration Image - Show only on first step (since same image for all steps)
                val stepImageRes = step.imageRes
                if (stepImageRes != null && step.stepNumber == 1) {
                    cardStepImage.visibility = View.VISIBLE
                    ivStepIllustration.setImageResource(stepImageRes)
                } else {
                    cardStepImage.visibility = View.GONE
                }

                // Duration display
                if (!step.duration.isNullOrEmpty()) {
                    tvDuration.visibility = View.VISIBLE
                    tvDuration.text = step.duration
                } else {
                    tvDuration.visibility = View.GONE
                }

                // Critical warning
                ivCriticalWarning.visibility = if (step.isCritical) View.VISIBLE else View.GONE

                // Detailed instructions - Enhanced display
                if (!step.detailedInstructions.isNullOrEmpty()) {
                    tvDetailedInstructions.text = step.detailedInstructions

                    // Initially hidden, can be expanded by clicking "More Details" button
                    if (expandedSteps.contains(position)) {
                        layoutDetailedInstructions.visibility = View.VISIBLE
                    } else {
                        layoutDetailedInstructions.visibility = View.GONE
                    }
                } else {
                    layoutDetailedInstructions.visibility = View.GONE
                }

                // Required tools
                if (!step.requiredTools.isNullOrEmpty()) {
                    layoutRequiredTools.visibility = View.VISIBLE
                    populateRequiredTools(step.requiredTools!!)
                } else {
                    layoutRequiredTools.visibility = View.GONE
                }

                // Tips - Enhanced display
                if (!step.tips.isNullOrEmpty()) {
                    layoutTips.visibility = View.VISIBLE
                    populateTips(step.tips!!)
                } else {
                    layoutTips.visibility = View.GONE
                }

                // Warnings - Enhanced display
                if (!step.warnings.isNullOrEmpty()) {
                    layoutWarnings.visibility = View.VISIBLE
                    populateWarnings(step.warnings!!)
                } else {
                    layoutWarnings.visibility = View.GONE
                }

                setupClickListeners(step, position)
            }
        }

        private fun populateRequiredTools(tools: List<String>) {
            binding.flexboxTools.removeAllViews()
            tools.forEach { tool ->
                val chip = Chip(binding.root.context).apply {
                    text = tool
                    setChipBackgroundColorResource(R.color.accent_light)
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                }
                binding.flexboxTools.addView(chip)
            }
        }

        private fun populateTips(tips: List<String>) {
            binding.layoutTipsList.removeAllViews()
            tips.forEach { tip ->
                val tipView = TextView(binding.root.context).apply {
                    text = "• $tip"
                    textSize = 13f
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    setPadding(0, 4, 0, 4)
                }
                binding.layoutTipsList.addView(tipView)
            }
        }

        private fun populateWarnings(warnings: List<String>) {
            binding.layoutWarningsList.removeAllViews()
            warnings.forEach { warning ->
                val warningView = TextView(binding.root.context).apply {
                    text = "⚠ $warning"
                    textSize = 13f
                    setTextColor(ContextCompat.getColor(context, R.color.warning))
                    setPadding(0, 4, 0, 4)
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                binding.layoutWarningsList.addView(warningView)
            }
        }

        private fun setupClickListeners(step: GuideStep, position: Int) {
            binding.apply {
                cardStep.setOnClickListener {
                    toggleExpansion(position)
                    onStepExpanded(step)
                }

                tvStepNumber.setOnClickListener {
                    markCompleted(step)
                }

                // More Details button to toggle detailed instructions
                btnMoreDetails.setOnClickListener {
                    toggleExpansion(position)
                    onStepExpanded(step)
                }

                // Update button text based on expansion state
                updateMoreDetailsButton(position)
            }
        }

        private fun updateMoreDetailsButton(position: Int) {
            val isExpanded = expandedSteps.contains(position)
            binding.btnMoreDetails.apply {
                text = if (isExpanded) "Less Details" else "More Details"
                setIconResource(if (isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more)
            }
        }

        private fun toggleExpansion(position: Int) {
            val isExpanded = expandedSteps.contains(position)
            if (isExpanded) {
                expandedSteps.remove(position)
                collapseStep()
            } else {
                expandedSteps.add(position)
                expandStep()
            }
        }

        private fun expandStep() {
            binding.layoutDetailedInstructions.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }

        private fun collapseStep() {
            binding.layoutDetailedInstructions.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction {
                    binding.layoutDetailedInstructions.visibility = View.GONE
                }
                .start()
        }

        private fun markCompleted(step: GuideStep) {
            binding.tvStepNumber.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction {
                    binding.tvStepNumber.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }
                .start()

            onStepCompleted(step)
        }

        fun playEntranceAnimation() {
            binding.cardStep.apply {
                alpha = 0f
                translationY = 100f
                animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setStartDelay((bindingAdapterPosition * 150).toLong())
                    .setInterpolator(DecelerateInterpolator())
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
