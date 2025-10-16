package com.example.firstaidapp.ui.guides

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

                // Note: Image display functionality would require adding image views to the layout
                // Currently the layout doesn't include cardStepImage or ivStepImage
                // TODO: Add image display capability to layout if needed

                // Handle detailed instructions
                if (!step.detailedInstructions.isNullOrEmpty()) {
                    layoutDetailedInstructions.visibility = View.VISIBLE
                    tvDetailedInstructions.text = step.detailedInstructions
                } else {
                    layoutDetailedInstructions.visibility = View.GONE
                }

                // Handle required tools
                if (!step.requiredTools.isNullOrEmpty()) {
                    layoutRequiredTools.visibility = View.VISIBLE
                    // Remove all existing views first
                    val flexboxContainer = layoutRequiredTools.findViewById<ViewGroup>(R.id.flexboxTools)
                    flexboxContainer?.removeAllViews()

                    step.requiredTools.forEach { tool ->
                        val chip = com.google.android.material.chip.Chip(root.context).apply {
                            text = tool
                            setChipBackgroundColorResource(R.color.primary_light)
                            setTextColor(root.context.getColor(R.color.primary))
                        }
                        flexboxContainer?.addView(chip)
                    }
                } else {
                    layoutRequiredTools.visibility = View.GONE
                }

                // Handle tips
                if (!step.tips.isNullOrEmpty()) {
                    layoutTips.visibility = View.VISIBLE
                    val tipsContainer = layoutTips.findViewById<ViewGroup>(R.id.layoutTipsList)
                    tipsContainer?.removeAllViews()

                    step.tips.forEach { tip ->
                        val tipView = android.widget.TextView(root.context).apply {
                            text = "• $tip"
                            textSize = 14f
                            setTextColor(root.context.getColor(R.color.text_secondary))
                            setPadding(0, 4, 0, 4)
                        }
                        tipsContainer?.addView(tipView)
                    }
                } else {
                    layoutTips.visibility = View.GONE
                }

                // Handle warnings if available
                if (!step.warnings.isNullOrEmpty()) {
                    // Show warning section if available
                    step.warnings.forEach { warning ->
                        // Add warning to UI (implementation depends on layout)
                    }
                }

                // Set up click listeners for expansion/interaction
                setupStepInteractions(step)

                // Apply entrance animation
                applyEntranceAnimation()
            }
        }

        private fun getStepImageResource(step: GuideStep): Int {
            return when {
                // CPR Guide Images
                step.guideId == "cpr_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.cpr_check_responsiveness
                    2 -> R.drawable.emergency_call
                    3 -> R.drawable.cpr_positioning
                    4 -> R.drawable.cpr_hand_position
                    5 -> R.drawable.cpr_compressions
                    6 -> R.drawable.cpr_compression
                    else -> R.drawable.ic_cpr
                }

                // Choking Guide Images
                step.guideId == "choking_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.choking_assessment
                    2 -> R.drawable.heimlich_position
                    3 -> R.drawable.heimlich_thrusts
                    else -> R.drawable.ic_choking
                }

                // Bleeding Guide Images
                step.guideId == "bleeding_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.emergency_call
                    2 -> R.drawable.cuts_pressure
                    else -> R.drawable.ic_bleeding
                }

                // Burns Guide Images
                step.guideId == "burns_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.ic_safety
                    2 -> R.drawable.burn_cooling
                    3 -> R.drawable.ic_medical_cross
                    else -> R.drawable.ic_burns
                }

                // Heart Attack Guide Images
                step.guideId == "heart_attack_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.emergency_call
                    2 -> R.drawable.ic_heart_attack
                    else -> R.drawable.ic_heart_attack
                }

                // Stroke Guide Images
                step.guideId == "stroke_guide" -> when (step.stepNumber) {
                    1 -> R.drawable.emergency_call
                    2 -> R.drawable.ic_timer
                    else -> R.drawable.ic_stroke
                }

                // Default images based on guide type
                step.guideId == "fractures_guide" -> R.drawable.ic_fractures
                step.guideId == "poisoning_guide" -> R.drawable.ic_poisoning
                step.guideId == "shock_guide" -> R.drawable.ic_shock
                step.guideId == "allergic_reaction_guide" -> R.drawable.ic_allergic_reaction
                step.guideId == "sprains_strains_guide" -> R.drawable.ic_sprain
                step.guideId == "hypothermia_guide" -> R.drawable.ic_hypothermia
                step.guideId == "heat_exhaustion_guide" -> R.drawable.ic_heat_exhaustion
                step.guideId == "seizures_guide" -> R.drawable.ic_seizure
                step.guideId == "bites_stings_guide" -> R.drawable.ic_bites_stings
                step.guideId == "asthma_attack_guide" -> R.drawable.ic_asthma
                step.guideId == "diabetic_emergencies_guide" -> R.drawable.ic_diabetes
                step.guideId == "drowning_guide" -> R.drawable.ic_drowning
                step.guideId == "nosebleeds_guide" -> R.drawable.ic_nosebleed
                step.guideId == "eye_injuries_guide" -> R.drawable.ic_eye_injury

                // Step type based fallbacks
                step.stepType == StepType.EMERGENCY_CALL -> R.drawable.emergency_call
                step.stepType == StepType.CALL -> R.drawable.ic_phone
                step.stepType == StepType.SAFETY -> R.drawable.ic_safety
                step.stepType == StepType.CHECK -> R.drawable.ic_search

                // Default medical icon
                else -> R.drawable.ic_medical_default
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

                // Note: Video play functionality would require adding a FAB to the layout
                // Currently the layout doesn't include fabPlayVideo
                // TODO: Add video play button to layout if video functionality is needed
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
